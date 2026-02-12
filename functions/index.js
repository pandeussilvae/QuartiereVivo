const {onDocumentUpdated} = require("firebase-functions/v2/firestore");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");

admin.initializeApp();

const STATUS_FIELD = "stato";
const ALLOWED_STATUS = new Set(["nuova", "in_carico", "risolta"]);

exports.notifyStatusChange = onDocumentUpdated("segnalazioni/{segnalazioneId}", async (event) => {
  const before = event.data.before.data();
  const after = event.data.after.data();

  if (!before || !after) {
    return;
  }

  const beforeStatus = normalizeStatus(before[STATUS_FIELD]);
  const afterStatus = normalizeStatus(after[STATUS_FIELD]);
  if (!afterStatus || beforeStatus === afterStatus) {
    return;
  }

  const segnalazioneId = event.params.segnalazioneId;
  const topicTargets = [
    after.categoria ? `category_${sanitize(after.categoria)}` : null,
    `segnalazione_${sanitize(segnalazioneId)}`,
  ].filter(Boolean);

  const notification = {
    title: "Aggiornamento segnalazione",
    body: `${after.titolo || "Segnalazione"}: stato aggiornato a ${afterStatus}`,
  };

  const dataPayload = {
    segnalazioneId,
    stato: afterStatus,
  };

  await Promise.all(topicTargets.map((topic) => admin.messaging().send({
    topic,
    notification,
    data: dataPayload,
  })));

  logger.info("Notifiche stato inviate", {segnalazioneId, topicTargets, stato: afterStatus});
});

function normalizeStatus(value) {
  const normalized = String(value || "").trim().toLowerCase();
  return ALLOWED_STATUS.has(normalized) ? normalized : null;
}

function sanitize(value) {
  return String(value).toLowerCase().replace(/\s+/g, "_").replace(/[^a-z0-9_-]/g, "");
}
