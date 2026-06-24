package com.fullstack.servicioTecnico.config;

import com.fullstack.servicioTecnico.model.ServicioTecnico;
import com.fullstack.servicioTecnico.repository.ServicioTecnicoRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

@Component
public class DataLoader implements CommandLineRunner {

    private final ServicioTecnicoRepository servicioTecnicoRepository;
    private final Faker faker = new Faker(new Locale("es"));
    public DataLoader(ServicioTecnicoRepository servicioTecnicoRepository) {this.servicioTecnicoRepository = servicioTecnicoRepository;}

    @Override
    public void run(String... args){
        for(int i = 1; i <= 10; i++){
            ServicioTecnico s = new ServicioTecnico();
            s.setPedidoId(faker.number().numberBetween(1,10));
            s.setNumeroSerie(
                    "SN-" + faker.number().digits(6)
            );
            s.setNombreCliente(
                    faker.name().fullName()
            );
            s.setNombreProducto(
                    faker.options().option(
                            "Yamaha P45",
                            "Roland FP10",
                            "Fender Stratocaster"
                    )
            );
            s.setFalla(faker.options().option(
                    "No enciende",
                    "Teclas dañadas",
                    "Cable defectuoso",
                    "Problema de sonido",
                    "Falla eléctrica",
                    "Pantalla no responde",
                    "Botones averiados",
                    "Conexión intermitente"
            ));
            s.setDescripcion(faker.options().option(
                    "El instrumento no enciende al conectarlo a la corriente.",
                    "Se detectan fallas en varias teclas durante su uso.",
                    "El equipo presenta cortes de sonido intermitentes.",
                    "La conexión de alimentación está dañada.",
                    "El instrumento emite ruido constante al utilizarlo.",
                    "La pantalla principal no responde correctamente.",
                    "Se observa desgaste prematuro en componentes internos.",
                    "El producto requiere diagnóstico técnico especializado."
            ));
            s.setFechaIngreso(
                    Date.from(
                            LocalDate.now()
                                    .minusDays(faker.number().numberBetween(1, 90))
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                    )
            );
            s.setEstado(faker.options().option("recibido","entregado"));
            servicioTecnicoRepository.save(s);
        }

    }



}
