const {onDocumentUpdated} = require("firebase-functions/v2/firestore");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");

admin.initializeApp();

exports.notifyStatusChange = onDocumentUpdated("segnalazioni/{segnalazioneId}", async (event) => {
  const before = event.data.before.data();
  const after = event.data.after.data();

  if (!before || !after || before.status === after.status) {
    return;
  }

  const segnalazioneId = event.params.segnalazioneId;
  const topicTargets = [
    after.categoria ? `category_${sanitize(after.categoria)}` : null,
    after.zona ? `zone_${sanitize(after.zona)}` : null,
    `segnalazione_${sanitize(segnalazioneId)}`,
  ].filter(Boolean);

  const notification = {
    title: "Aggiornamento segnalazione",
    body: `${after.titolo || "Segnalazione"}: stato aggiornato a ${after.status}`,
  };

  const dataPayload = {
    segnalazioneId,
    status: String(after.status || ""),
  };

  await Promise.all(topicTargets.map((topic) => admin.messaging().send({
    topic,
    notification,
    data: dataPayload,
  })));

  logger.info("Notifiche status inviate", {segnalazioneId, topicTargets, status: after.status});
});

function sanitize(value) {
  return String(value).toLowerCase().replace(/\s+/g, "_").replace(/[^a-z0-9_-]/g, "");
}
