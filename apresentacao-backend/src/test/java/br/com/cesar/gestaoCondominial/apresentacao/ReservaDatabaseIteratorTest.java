package br.com.cesar.gestaoCondominial.apresentacao;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.iterator.ReservaDatabaseIterator;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReservaDatabaseIteratorTest {

    @Test
    void testEmptyReservations() {
        ReservaRepository repository = mock(ReservaRepository.class);
        UsuarioId usuarioId = new UsuarioId(1L);

        when(repository.buscarPorUsuarioPaginado(eq(usuarioId), eq(5), eq(0)))
                .thenReturn(Collections.emptyList());

        ReservaDatabaseIterator iterator = new ReservaDatabaseIterator(repository, usuarioId, 5);

        assertFalse(iterator.hasNext());
    }

    @Test
    void testLessThanPageSize() {
        ReservaRepository repository = mock(ReservaRepository.class);
        UsuarioId usuarioId = new UsuarioId(1L);

        List<Reserva> mockList = new ArrayList<>();
        mockList.add(mock(Reserva.class));
        mockList.add(mock(Reserva.class));

        when(repository.buscarPorUsuarioPaginado(eq(usuarioId), eq(5), eq(0)))
                .thenReturn(mockList);

        ReservaDatabaseIterator iterator = new ReservaDatabaseIterator(repository, usuarioId, 5);

        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testMultiplePages() {
        ReservaRepository repository = mock(ReservaRepository.class);
        UsuarioId usuarioId = new UsuarioId(1L);

        List<Reserva> page1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            page1.add(mock(Reserva.class));
        }

        List<Reserva> page2 = new ArrayList<>();
        page2.add(mock(Reserva.class));
        page2.add(mock(Reserva.class));

        when(repository.buscarPorUsuarioPaginado(eq(usuarioId), eq(5), eq(0)))
                .thenReturn(page1);
        when(repository.buscarPorUsuarioPaginado(eq(usuarioId), eq(5), eq(5)))
                .thenReturn(page2);

        ReservaDatabaseIterator iterator = new ReservaDatabaseIterator(repository, usuarioId, 5);

        int count = 0;
        while (iterator.hasNext()) {
            assertNotNull(iterator.next());
            count++;
        }

        assertEquals(7, count);
        verify(repository, times(1)).buscarPorUsuarioPaginado(usuarioId, 5, 0);
        verify(repository, times(1)).buscarPorUsuarioPaginado(usuarioId, 5, 5);
    }
}
