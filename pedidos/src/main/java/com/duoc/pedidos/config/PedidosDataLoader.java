package com.duoc.pedidos.config;

import com.duoc.pedidos.model.Pedido;
import com.duoc.pedidos.repository.PedidoRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

@Component
public class PedidosDataLoader implements CommandLineRunner {

    private final PedidoRepository pedidoRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public PedidosDataLoader(PedidoRepository pedidoRepository) {this.pedidoRepository = pedidoRepository;}

    @Override
    public void run(String... args) {
        for (int i = 1; i < 5; i++) {
            Pedido pedido = new Pedido();
            pedido.setClienteId(faker.number().numberBetween(1,10));
            pedido.setProductoId(faker.number().numberBetween(1,10));
            pedido.setNombreCliente(
                    faker.name().fullName()
            );
            pedido.setNumeroSerie(
                    "SN-" + faker.number().digits(6)
            );
            pedido.setNombreProducto(
                    faker.options().option(
                            "Yamaha P45",
                            "Fender Stratocaster",
                            "Roland FP-10",
                            "Casio CT-S300"
                    )
            );
            pedido.setTipoInstrumento(
                    faker.options().option(
                            "Piano",
                            "Guitarra",
                            "Batería",
                            "Teclado"
                    )
            );
            pedido.setDireccion(faker.address().fullAddress());
            pedido.setDescripcion(
                    faker.lorem().sentence()
            );
            pedido.setPrecioArriendo(
                    faker.number().numberBetween(10000, 50000)
            );
            pedido.setEstado(
                    faker.options().option(
                            "Disponible",
                            "Reservado",
                            "Entregado"
                    )
            );
            pedido.setVentaArriendo(
                    faker.options().option("Venta", "Arriendo")
            );
            pedido.setPrecioEnvio(faker.number().numberBetween(100000,100000));
            pedido.setFechaPedido(new Date());
            pedido.setFechaRegistro(new Date());
            pedidoRepository.save(pedido);
        }
    }
}