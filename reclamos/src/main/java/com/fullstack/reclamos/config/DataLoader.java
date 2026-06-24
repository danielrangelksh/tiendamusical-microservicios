package com.fullstack.reclamos.config;

import com.fullstack.reclamos.model.Reclamos;
import com.fullstack.reclamos.repository.ReclamosRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

@Component
public class DataLoader implements CommandLineRunner {

    private final ReclamosRepository reclamosRepository;
    private final Faker faker =  new Faker(new Locale("es"));
    public DataLoader(ReclamosRepository reclamosRepository) {this.reclamosRepository = reclamosRepository;}

    @Override
    public void run(String... args) {
        for (int i = 1; i <= 10; i++) {
            Reclamos reclamos = new Reclamos();
            reclamos.setClienteId(faker.number().numberBetween(1,15));
            reclamos.setNombreCliente(
                    faker.name().fullName()
            );
            reclamos.setAsunto(faker.options().option(
                    "Producto defectuoso",
                    "Retraso en la entrega",
                    "Cobro incorrecto",
                    "Problema con garantía",
                    "Instrumento dañado",
                    "Pedido incompleto",
                    "Error en la facturación",
                    "Producto equivocado"
            ));
            reclamos.setDescripcion(
                    faker.options().option(
                            "El instrumento llegó con daños visibles en su estructura.",
                            "El pedido fue entregado fuera del plazo informado.",
                            "Se realizó un cobro mayor al indicado en la compra.",
                            "La garantía no fue aplicada correctamente.",
                            "El producto recibido no corresponde al solicitado.",
                            "Faltan accesorios incluidos en la descripción de venta.",
                            "El instrumento presenta fallas desde el primer uso.",
                            "Se requiere revisión del caso y una solución."
                    )
            );
            reclamos.setFechaRegistro(
                    Date.from(
                            LocalDate.now()
                                    .minusDays(faker.number().numberBetween(1, 365))
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                    )
            );
            reclamosRepository.save(reclamos);
        }
    }
}
