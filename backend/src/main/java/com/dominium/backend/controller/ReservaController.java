package com.dominium.backend.controller;

import com.dominium.backend.entity.Reserva;
import com.dominium.backend.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService service;

    @PostMapping
    public Reserva criar(@RequestBody Reserva reserva) {
        return service.criarReserva(reserva);
    }

    @GetMapping("/unidade/{id}")
    public List<Reserva> listar(@PathVariable Long id) {
        return service.listarPorUnidade(id);
    }

    @PutMapping("/{id}")
    public Reserva atualizar(@PathVariable Long id, @RequestBody Reserva reserva) {
        return service.atualizarReserva(id, reserva);
    }

    @DeleteMapping("/{id}")
    public void cancelar(@PathVariable Long id) {
        service.cancelarReserva(id);
    }
}
