package com.fullstack.servicioTecnico.service;

import com.fullstack.servicioTecnico.dto.ServicioTecnicoRequest;
import com.fullstack.servicioTecnico.webClient.PedidosClient;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fullstack.servicioTecnico.model.ServicioTecnico;
import com.fullstack.servicioTecnico.repository.ServicioTecnicoRepository;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServicioTecnicoService {

    private static final Logger log = LoggerFactory.getLogger(ServicioTecnico.class);

    @Autowired
    private ServicioTecnicoRepository servicioTecnicoRepository;

    @Autowired
    private PedidosClient pedidosClient;

    public List<ServicioTecnico> listarServicioTecnico(){
        log.info("Listando servicios");
        return servicioTecnicoRepository.findAll();
    }

    public ServicioTecnico buscarPorId(Integer id){
        log.info("Buscando servicios por id: {}", id);
        return servicioTecnicoRepository.findById(id).orElse(null);
    }

    public ServicioTecnico guardar(ServicioTecnico servicioTecnico){
        log.info("Guardando servicio técnico");
        return servicioTecnicoRepository.save(servicioTecnico);
    }

    public ServicioTecnico crearDesdeRequest(ServicioTecnicoRequest request, String token) {
        Map<String, Object> pedidos = pedidosClient.obtenerPedidoId(request.getPedidoId(), token);
        if(pedidos == null || pedidos.isEmpty()) {
            throw new RuntimeException("Error: El pedido no existe en el catálogo principal.");
        }
        ServicioTecnico servicioTecnico = new ServicioTecnico();
        servicioTecnico.setFalla(request.getFalla());
        servicioTecnico.setDescripcion(request.getDescripcion());
        servicioTecnico.setEstado(request.getEstado());
        servicioTecnico.setPedidoId(request.getPedidoId());
        servicioTecnico.setFechaIngreso(request.getFechaIngreso());
        servicioTecnico.setNombreCliente(pedidos.get("nombreCliente").toString());
        servicioTecnico.setNumeroSerie(pedidos.get("numeroSerie").toString());
        servicioTecnico.setNombreProducto(pedidos.get("nombreProducto").toString());
        log.info("Servicio guardado correctamente: {}", servicioTecnico);
        return guardar(servicioTecnico);
    }

    public boolean eliminar(Integer id){
        log.info("Eliminando servicio por id: {}", id);
        ServicioTecnico servicioTecnico = buscarPorId(id);
        if(servicioTecnico == null){
            log.warn("Servicio no encontrado con id: {}", id);
            return false;
        }
        servicioTecnicoRepository.delete(servicioTecnico);
        log.info("Servicio eliminado correctamente.");
        return true;
    }
}
