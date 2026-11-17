const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_student_1";
const BOB_UID = "bob_student_2";
const CHARLIE_UID = "charlie_student_3";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validPostCreatePayload(uid, offsetMs = 5000) {
  const ts = new Date(Date.now() - offsetMs);
  return {
    userId: uid,
    authorName: "أحمد محمد",
    authorTrack: "القسم العلمي",
    subjectTitle: "الفيزياء",
    title: "كيف نحسب العجلة المركزية في المسار الدائري؟",
    body: "أرجو توضيح العلاقة بين السرعة الخطية ونصف القطر في الحركة الدائرية المنتظمة.",
    status: "OPEN",
    visibility: "PUBLIC",
    repliesCount: 0,
    helpfulCount: 0,
    teacherReviewStatus: "PENDING",
    teacherReviewNote: "",
    createdAt: ts,
    updatedAt: ts,
  };
}

function validReplyCreatePayload(postId, uid, offsetMs = 3000) {
  const ts = new Date(Date.now() - offsetMs);
  return {
    postId,
    userId: uid,
    authorName: "سارة خالد",
    body: "تُحسب العجلة المركزية من العلاقة ت = ع² / نق وتكون دائماً نحو المركز.",
    postVisibility: "PUBLIC",
    teacherBadge: "NONE",
    teacherFeedback: "",
    markedHelpfulByAuthor: false,
    createdAt: ts,
    updatedAt: ts,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read, list, or write forum_posts or replies", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("forum_posts").doc("post_1").set(validPostCreatePayload(ALICE_UID));
  });

  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("forum_posts").doc("post_1").get());
  await assertFails(unauthDb.collection("forum_posts").where("visibility", "==", "PUBLIC").get());
  await assertFails(
    unauthDb.collection("forum_posts").doc("post_unauth").set(validPostCreatePayload(ALICE_UID))
  );
  await assertFails(
    unauthDb.collection("forum_posts").doc("post_1").collection("replies").where("postVisibility", "==", "PUBLIC").get()
  );
});

test("Authenticated student: valid ForumRepository post create, AI teacher review update, and SOLVED terminal lock", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const postRef = aliceDb.collection("forum_posts").doc("post_valid_1");

  // 1. Valid create matching ForumRepository payload (no `id` field)
  await assertSucceeds(postRef.set(validPostCreatePayload(ALICE_UID)));

  // 2. Owner updates AI teacher review fields while status == OPEN
  await assertSucceeds(
    postRef.update({
      teacherReviewStatus: "GUIDED",
      teacherReviewNote: "المفهوم الأساسي: الحركة الدائرية المنتظمة\nتذكر أن اتجاه السرعة المماسية يتغير باستمرار.",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // 3. Owner marks post as SOLVED
  await assertSucceeds(
    postRef.update({
      status: "SOLVED",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // 4. Terminal lock: after status == SOLVED, owner cannot reopen or edit body/title
  await assertFails(
    postRef.update({
      status: "OPEN",
      updatedAt: new Date(Date.now() - 100),
    })
  );
});

test("ForumPost security guards reject @DocumentId field, spoofed uid, pre-seeded counts, and future timestamps", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Reject extra `id` field (@DocumentId bug)
  await assertFails(
    aliceDb.collection("forum_posts").doc("post_with_id").set({
      id: "post_with_id",
      ...validPostCreatePayload(ALICE_UID),
    })
  );

  // Reject userId spoofing
  await assertFails(
    aliceDb.collection("forum_posts").doc("post_spoof").set(validPostCreatePayload(BOB_UID))
  );

  // Reject non-zero initial repliesCount / pre-verified teacherReviewStatus
  await assertFails(
    aliceDb.collection("forum_posts").doc("post_fake_count").set({
      ...validPostCreatePayload(ALICE_UID),
      repliesCount: 10,
    })
  );
  await assertFails(
    aliceDb.collection("forum_posts").doc("post_fake_ai").set({
      ...validPostCreatePayload(ALICE_UID),
      teacherReviewStatus: "APPROVED",
    })
  );

  // Reject tampered future timestamp
  const futureDate = new Date(Date.now() + 120000);
  await assertFails(
    aliceDb.collection("forum_posts").doc("post_future").set({
      ...validPostCreatePayload(ALICE_UID),
      createdAt: futureDate,
      updatedAt: futureDate,
    })
  );
});

test("Peer reply atomic batch + AI teacher feedback + post-author helpful toggle + cross-user isolation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const charlieDb = testEnv.authenticatedContext(CHARLIE_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("forum_posts").doc("post_thread_1").set(validPostCreatePayload(ALICE_UID))
  );

  // Bob submits atomic WriteBatch (create reply + increment repliesCount by 1)
  const bobPostRef = bobDb.collection("forum_posts").doc("post_thread_1");
  const bobReplyRef = bobPostRef.collection("replies").doc("reply_bob_1");
  const batch = bobDb.batch();
  batch.set(bobReplyRef, validReplyCreatePayload("post_thread_1", BOB_UID));
  batch.update(bobPostRef, {
    repliesCount: 1,
    updatedAt: new Date(Date.now() - 1000),
  });
  await assertSucceeds(batch.commit());

  // Bob updates AI teacher verification on his own reply
  await assertSucceeds(
    bobReplyRef.update({
      teacherBadge: "VERIFIED",
      teacherFeedback: "إجابة دقيقة وممتازة، أحسنت يا بطل!",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Post owner (Alice) can toggle markedHelpfulByAuthor on Bob's reply
  await assertSucceeds(
    aliceDb.collection("forum_posts").doc("post_thread_1").collection("replies").doc("reply_bob_1").update({
      markedHelpfulByAuthor: true,
      updatedAt: new Date(Date.now() - 200),
    })
  );

  // Post owner (Alice) CANNOT alter Bob's reply body
  await assertFails(
    aliceDb.collection("forum_posts").doc("post_thread_1").collection("replies").doc("reply_bob_1").update({
      body: "تم التعديل بواسطة صاحب السؤال",
      updatedAt: new Date(Date.now() - 100),
    })
  );

  // Unrelated student (Charlie) CANNOT edit/delete Alice's post or Bob's reply, nor jump helpfulCount by +5
  await assertFails(
    charlieDb.collection("forum_posts").doc("post_thread_1").update({
      title: "عنوان معدل",
      updatedAt: new Date(Date.now() - 100),
    })
  );
  await assertFails(
    charlieDb.collection("forum_posts").doc("post_thread_1").update({
      helpfulCount: 5,
      updatedAt: new Date(Date.now() - 100),
    })
  );
  await assertFails(charlieDb.collection("forum_posts").doc("post_thread_1").delete());
  await assertFails(
    charlieDb.collection("forum_posts").doc("post_thread_1").collection("replies").doc("reply_bob_1").delete()
  );

  // Legitimate peer helpfulCount +1 succeeds
  await assertSucceeds(
    charlieDb.collection("forum_posts").doc("post_thread_1").update({
      helpfulCount: 1,
      updatedAt: new Date(Date.now() - 100),
    })
  );
});

test("Client query alignment: filtered queries succeed while unfiltered list queries fail", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("forum_posts").doc("post_q1").set(validPostCreatePayload(ALICE_UID));
    await db
      .collection("forum_posts")
      .doc("post_q1")
      .collection("replies")
      .doc("rep_q1")
      .set(validReplyCreatePayload("post_q1", BOB_UID));
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  // Matches ForumRepository.observePublicPosts()
  await assertSucceeds(bobDb.collection("forum_posts").where("visibility", "==", "PUBLIC").get());
  // Unfiltered list on forum_posts must fail
  await assertFails(bobDb.collection("forum_posts").get());

  // Matches ForumRepository.observeReplies(postId)
  await assertSucceeds(
    bobDb
      .collection("forum_posts")
      .doc("post_q1")
      .collection("replies")
      .where("postVisibility", "==", "PUBLIC")
      .get()
  );
  // Unfiltered list on replies subcollection must fail
  await assertFails(bobDb.collection("forum_posts").doc("post_q1").collection("replies").get());
});
