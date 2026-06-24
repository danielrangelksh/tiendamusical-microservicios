package com.fullstack.productos.config;

import com.fullstack.productos.model.Producto;
import com.fullstack.productos.repository.ProductoRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final Faker faker = new Faker(new Locale("es"));
    private final List<String> tipos = List.of("Cuerdas", "Percusión", "Teclado", "Amplificación", "Accesorio");

    public DataLoader(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            Producto producto = new Producto();
            producto.setNumeroSerie(faker.numerify("SN-#####"));
            producto.setNombreProducto(faker.music().instrument());
            producto.setTipoInstrumento(tipos.get(faker.number().numberBetween(0, tipos.size())));
            producto.setDescripcion(faker.lorem().sentence());
            producto.setPrecioInstrumento(faker.number().numberBetween(50000, 2000000));
            producto.setVentaArriendo(faker.bool().bool() ? "VENTA" : "ARRIENDO");
            producto.setEstado("DISPONIBLE");
            productoRepository.save(producto);
        }
    }
}
