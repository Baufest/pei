package com.pei.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
}

/*
Revisión de Transferencias Internacionales

Descripción

Objetivo: Aplicar reglas especiales para transacciones internacionales.

"Países de riesgo (ej.: Venezuela, Rusia): siempre requieren aprobación manual 
si es el caso la transaccion debera quedar en un estado “requiere aprobacion”.
 Para montos >$50,000, notificar al área de Compliance mediante un correo a compliance@banco.com.ar 
 pero permitir avanzar."
Conclusión técnica:
Hardcodear lista de países y crear una alerta que permita al circuito de 
aprobaciones continuar si esta ok y enviar alerta cuando lo requiera
 */


 
/*
Estados de transaction: APROBADA, RECHAZADA, REQUIERE APROBACION

SI ES PAIS DE RIESGO: TRANSACTION.STATUS(REQUIERE_APROBACION)
SI ES INTERNACIONAL: IF MONTO > 50,000 NOTIFICAR AL AREA DE COMPLIANCE, PERO PERMITIR AVANZAR (PENDIENTE O APROBADA)

NOTIFICACION COMPLIANCE: POR EMAIL

//SE AGREGA PAIS EN ACCOUNT
//SE AGREGA isInternational EN TRANSACTION ; SE PREGUNTA AL DESTINATION ACOUNT SI PAIS != PAIS ORIGEN (ENTONCES ES INTERNACIONAL)
*/

