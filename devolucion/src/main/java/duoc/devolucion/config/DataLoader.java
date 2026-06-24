package duoc.devolucion.config;

import duoc.devolucion.model.Devolucion;
import duoc.devolucion.repository.DevolucionRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DataLoader implements CommandLineRunner {

    private final DevolucionRepository devolucionRepository;
    private final Faker faker = new Faker(new Locale("es"));
    public DataLoader(DevolucionRepository devolucionRepository) {this.devolucionRepository = devolucionRepository;}

    @Override
    public void run(String... args){
        for(int i = 0; i < 10; i++){
            Devolucion devolucion = new Devolucion();
            devolucion.setPedidoId(faker.number().numberBetween(1,10));
            devolucion.setRequerimiento(faker.options().option("Reembolso", "Cambio"));
            devolucion.setMotivo(faker.options().option(
                            "Producto llegó dañado",
                            "No corresponde al producto solicitado",
                            "Falla de fábrica",
                            "Problemas de funcionamiento",
                            "Producto incompleto",
                            "No cumple con las expectativas",
                            "Daños durante el transporte"
                    )
            );
            devolucion.setNombreCliente(
                    faker.name().fullName()
            );
            devolucion.setNumeroSerie(
                    "SN-" + faker.number().digits(6)
            );
            devolucion.setNombreProducto(
                    faker.options().option(
                            "Yamaha P45",
                            "Roland FP10",
                            "Fender Stratocaster"
                    )
            );
            devolucion.setPrecioInstrumento(
                    faker.number().numberBetween(100000, 900000)
            );
            devolucionRepository.save(devolucion);
        }
    }
}
