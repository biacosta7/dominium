package br.com.cesar.gestaoCondominial.apresentacao.dominium.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class DatabaseSeeder implements CommandLineRunner {

        private final JdbcTemplate jdbcTemplate;

        public DatabaseSeeder(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public void run(String... args) throws Exception {
                seedTiposOcorrencia();

                Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Integer.class);
                if (userCount != null && userCount > 0) {
                        System.out.println("Banco de dados já contém registros. Pulando seeding inicial.");
                        return;
                }

                System.out.println("Iniciando seeding do banco de dados com dados reais do protótipo...");

                insertUsuario("Marco Ribeiro", "marco.ribeiro@dominium.com", "123456", "(11) 98888-8888",
                                "111.111.111-11", "SINDICO");

                long robertoId = insertUsuario("Roberto Alves", "roberto.alves@parqueverde.com", "123456",
                                "(11) 97777-1001",
                                "222.222.222-01", "MORADOR");
                long anaId = insertUsuario("Ana Lima", "ana.lima@parqueverde.com", "123456", "(11) 97777-1002",
                                "222.222.222-02", "MORADOR");
                long jorgeId = insertUsuario("Jorge Santos", "jorge.santos@parqueverde.com", "123456",
                                "(11) 97777-1008",
                                "222.222.222-08", "MORADOR");
                long carlaId = insertUsuario("Carla Mendes", "carla.mendes@parqueverde.com", "123456",
                                "(11) 97777-1215",
                                "222.222.222-15", "MORADOR");
                long fernandaId = insertUsuario("Fernanda Costa", "fernanda.costa@parqueverde.com", "123456",
                                "(11) 97777-1304",
                                "222.222.222-04", "MORADOR");
                long pauloId = insertUsuario("Paulo Oliveira", "paulo.oliveira@parqueverde.com", "123456",
                                "(11) 97777-1312",
                                "222.222.222-12", "MORADOR");
                long rafaelId = insertUsuario("Rafael Lima", "rafael.lima@parqueverde.com", "123456", "(11) 97777-1421",
                                "222.222.222-21", "MORADOR");

                long unit101 = insertUnidade("101", "A", robertoId, "ADIMPLENTE", BigDecimal.ZERO);
                long unit102 = insertUnidade("102", "A", anaId, "ADIMPLENTE", BigDecimal.ZERO);
                long unit108 = insertUnidade("108", "A", jorgeId, "INADIMPLENTE", new BigDecimal("626.00"));
                long unit215 = insertUnidade("215", "B", carlaId, "ADIMPLENTE", BigDecimal.ZERO);
                long unit304 = insertUnidade("304", "C", fernandaId, "ADIMPLENTE", BigDecimal.ZERO);
                long unit312 = insertUnidade("312", "C", pauloId, "INADIMPLENTE", new BigDecimal("672.00"));
                long unit421 = insertUnidade("421", "D", rafaelId, "INADIMPLENTE", new BigDecimal("597.00"));

                insertVinculo(unit101, robertoId, "TITULAR", "ATIVO");
                insertVinculo(unit102, anaId, "TITULAR", "ATIVO");
                insertVinculo(unit108, jorgeId, "TITULAR", "ATIVO");
                insertVinculo(unit215, carlaId, "TITULAR", "ATIVO");
                insertVinculo(unit304, fernandaId, "TITULAR", "ATIVO");
                insertVinculo(unit312, pauloId, "TITULAR", "ATIVO");
                insertVinculo(unit421, rafaelId, "TITULAR", "ATIVO");

                for (int num = 103; num <= 120; num++) {
                        if (num == 108)
                                continue;
                        long dummyUserId = insertUsuario("Morador Apto " + num, "morador" + num + "@parqueverde.com",
                                        "123456",
                                        null, "333.333.333-" + String.format("%02d", num % 100), "MORADOR");
                        long dummyUnitId = insertUnidade(String.valueOf(num), "A", dummyUserId, "ADIMPLENTE",
                                        BigDecimal.ZERO);
                        insertVinculo(dummyUnitId, dummyUserId, "TITULAR", "ATIVO");

                        if (num <= 118) {
                                insertTaxa(dummyUnitId, new BigDecimal("580.00"), BigDecimal.ZERO,
                                                new BigDecimal("580.00"),
                                                "2026-03-15", "2026-03-14", "PAGO");
                        } else {
                                insertTaxa(dummyUnitId, new BigDecimal("580.00"), BigDecimal.ZERO,
                                                new BigDecimal("580.00"),
                                                "2026-03-15", null, "PENDENTE");
                        }
                }

                insertTaxa(unit101, new BigDecimal("580.00"), BigDecimal.ZERO, new BigDecimal("580.00"), "2026-03-15",
                                "2026-03-14", "PAGO");
                insertTaxa(unit102, new BigDecimal("580.00"), BigDecimal.ZERO, new BigDecimal("580.00"), "2026-03-15",
                                "2026-03-14", "PAGO");
                insertTaxa(unit108, new BigDecimal("580.00"), new BigDecimal("46.00"), new BigDecimal("626.00"),
                                "2026-02-15",
                                null, "ATRASADA");
                insertTaxa(unit215, new BigDecimal("580.00"), BigDecimal.ZERO, new BigDecimal("580.00"), "2026-03-15",
                                null,
                                "PENDENTE");
                insertTaxa(unit304, new BigDecimal("580.00"), BigDecimal.ZERO, new BigDecimal("580.00"), "2026-03-15",
                                "2026-03-13", "PAGO");
                insertTaxa(unit312, new BigDecimal("580.00"), new BigDecimal("92.00"), new BigDecimal("672.00"),
                                "2026-01-15",
                                null, "ATRASADA");
                insertTaxa(unit421, new BigDecimal("580.00"), new BigDecimal("17.00"), new BigDecimal("597.00"),
                                "2026-02-15",
                                null, "ATRASADA");

                long budget2026 = insertOrcamento(2026, new BigDecimal("120000.00"), new BigDecimal("31450.00"));

                insertDespesa("Serviço de Portaria Especializada", new BigDecimal("18500.00"), "2026-03-01", "PESSOAL",
                                "ORDINARIA", "APROVADA", budget2026);
                insertDespesa("Limpeza Técnica e Conservação", new BigDecimal("8450.00"), "2026-03-02", "UTILIDADES",
                                "ORDINARIA", "APROVADA", budget2026);
                insertDespesa("Manutenção de Elevadores Otis", new BigDecimal("4500.00"), "2026-03-01", "MANUTENCAO",
                                "ORDINARIA", "APROVADA", budget2026);

                insertDespesa("Pintura Externa e Restauro Fachada", new BigDecimal("15000.00"), "2026-03-05",
                                "MANUTENCAO",
                                "EXTRAORDINARIA", "PENDENTE", budget2026);

                System.out.println("Seeding do banco de dados concluído com sucesso!");
        }

        private void seedTiposOcorrencia() {
                Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tipos_ocorrencia", Integer.class);
                if (count != null && count > 0) {
                        return;
                }
                System.out.println("Inserindo tipos de ocorrência padrão...");
                String sql = "INSERT INTO tipos_ocorrencia (nome, valor_base_multa) VALUES (?, ?)";
                jdbcTemplate.update(sql, "Barulho Excessivo",            new BigDecimal("150.00"));
                jdbcTemplate.update(sql, "Descarte Irregular",           new BigDecimal("200.00"));
                jdbcTemplate.update(sql, "Vandalismo",                   new BigDecimal("500.00"));
                jdbcTemplate.update(sql, "Limpeza",                      new BigDecimal("100.00"));
                jdbcTemplate.update(sql, "Uso Indevido de Área Comum",   new BigDecimal("250.00"));
                jdbcTemplate.update(sql, "Estacionamento Irregular",     new BigDecimal("120.00"));
                jdbcTemplate.update(sql, "Animais sem Guia",             new BigDecimal("80.00"));
                jdbcTemplate.update(sql, "Outros",                       new BigDecimal("150.00"));
        }

        private long insertUsuario(String nome, String email, String senha, String telefone, String cpf, String tipo) {
                String sql = "INSERT INTO usuarios(nome, email, senha, telefone, cpf, tipo) VALUES (?, ?, ?, ?, ?, ?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, nome);
                        ps.setString(2, email);
                        ps.setString(3, senha);
                        ps.setString(4, telefone);
                        ps.setString(5, cpf);
                        ps.setString(6, tipo);
                        return ps;
                }, keyHolder);
                return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0;
        }

        private long insertUnidade(String numero, String bloco, long proprietarioId, String status,
                        BigDecimal saldoDevedor) {
                String sql = "INSERT INTO unidades(numero, bloco, proprietario_id, inquilino_id, status, saldo_devedor) VALUES (?, ?, ?, NULL, ?, ?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, numero);
                        ps.setString(2, bloco);
                        ps.setLong(3, proprietarioId);
                        ps.setString(4, status);
                        ps.setBigDecimal(5, saldoDevedor);
                        return ps;
                }, keyHolder);
                return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0;
        }

        private void insertVinculo(long unidadeId, long usuarioId, String tipo, String status) {
                String sql = "INSERT INTO vinculos_morador(unidade_id, usuario_id, tipo, status) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(sql, unidadeId, usuarioId, tipo, status);
        }

        private void insertTaxa(long unidadeId, BigDecimal base, BigDecimal multas, BigDecimal total, String due,
                        String paid, String status) {
                String sql = "INSERT INTO taxa_condominial (unidade_id, valor_base, valor_multas, valor_total, data_vencimento, data_pagamento, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                java.sql.Date sqlDue = java.sql.Date.valueOf(due);
                java.sql.Timestamp sqlPaid = paid != null ? java.sql.Timestamp.valueOf(paid + " 12:00:00") : null;
                jdbcTemplate.update(sql, unidadeId, base, multas, total, sqlDue, sqlPaid, status);
        }

        private long insertOrcamento(int ano, BigDecimal total, BigDecimal gasto) {
                String sql = "INSERT INTO orcamentos(ano, valor_total, valor_gasto) VALUES (?, ?, ?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, ano);
                        ps.setBigDecimal(2, total);
                        ps.setBigDecimal(3, gasto);
                        return ps;
                }, keyHolder);
                return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0;
        }

        private void insertDespesa(String descricao, BigDecimal valor, String data, String categoria, String tipo,
                        String status, long orcamentoId) {
                String sql = "INSERT INTO despesas(descricao, valor, data, categoria, tipo, status, orcamento_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                java.sql.Date sqlData = java.sql.Date.valueOf(data);
                jdbcTemplate.update(sql, descricao, valor, sqlData, categoria, tipo, status, orcamentoId);
        }
}