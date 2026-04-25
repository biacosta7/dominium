// package com.dominium.backend.application.reservas.dto;

// import com.dominium.backend.domain.reservas.Reserva;

<<<<<<< HEAD
public record ReservaResponse(
        String id,
        String status
) {
    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId().toString(),
                reserva.getStatus().name()
        );
    }
}
=======
// public record ReservaResponse(
//         String id,
//         String status
// ) {
//     public static ReservaResponse from(Reserva reserva) {
//         return new ReservaResponse(
//                 reserva.getReservaId().toString(),
//                 reserva.getStatus().name()
//         );
//     }
// }
>>>>>>> origin/FD-37
