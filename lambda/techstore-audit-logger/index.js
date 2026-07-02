const REQUIRED_FIELDS = ["accion", "productoId", "nombre", "usuario", "fecha"];

export const handler = async (event) => {
  const batchItemFailures = [];

  for (const record of event.Records ?? []) {
    try {
      const evento = JSON.parse(record.body);

      const faltantes = REQUIRED_FIELDS.filter((campo) => evento[campo] === undefined);
      if (faltantes.length > 0) {
        throw new Error(`Evento de auditoría incompleto, faltan campos: ${faltantes.join(", ")}`);
      }

      console.log(
        JSON.stringify({
          mensaje: "Auditoría de inventario TechStore",
          accion: evento.accion,
          productoId: evento.productoId,
          nombre: evento.nombre,
          usuario: evento.usuario,
          fecha: evento.fecha,
          messageId: record.messageId,
        })
      );
    } catch (error) {
      console.error(`Error procesando mensaje SQS (messageId=${record.messageId}):`, error);
      batchItemFailures.push({ itemIdentifier: record.messageId });
    }
  }

  return { batchItemFailures };
};
