package com.example.restaurant.servicio;

import com.example.restaurant.dto.*;
import com.example.restaurant.entidades.*;
import com.example.restaurant.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private BebidaRepository bebidaRepository;

    @Autowired
    private EntradaRepository entradaRepository;

    // ========== OPERACIONES PARA COMIDAS ==========

    @Transactional(readOnly = true)
    public List<ComidaDTO> listarComidas() {
        return comidaRepository.findAll().stream()
                .map(this::convertToComidaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComidaDTO buscarComidaPorId(Integer id) {
        return comidaRepository.findById(id)
                .map(this::convertToComidaDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ComidaDTO> buscarComidasPorNombre(String nombre) {
        Comida comida = comidaRepository.findByNombreComida(nombre);
        if (comida != null) {
            return Collections.singletonList(convertToComidaDTO(comida));
        }
        return Collections.emptyList();
    }

    @Transactional
    public ComidaDTO guardarComida(ComidaDTO comidaDTO) {
        Comida comida = convertToComidaEntity(comidaDTO);
        comida = comidaRepository.save(comida);
        return convertToComidaDTO(comida);
    }

    @Transactional
    public void eliminarComida(Integer id) {
        comidaRepository.deleteById(id);
    }

    private ComidaDTO convertToComidaDTO(Comida comida) {
        ComidaDTO dto = new ComidaDTO();
        dto.setId(comida.getIdComida());
        dto.setNombre(comida.getNombreComida());
        dto.setTipo(comida.getTipoComida());
        dto.setPrecio(comida.getPrecio());
        return dto;
    }

    private Comida convertToComidaEntity(ComidaDTO dto) {
        Comida comida = new Comida();
        if (dto.getId() != null) {
            comida.setIdComida(dto.getId());
        }
        comida.setNombreComida(dto.getNombre());
        comida.setTipoComida(dto.getTipo());
        comida.setPrecio(dto.getPrecio());
        return comida;
    }

    // ========== OPERACIONES PARA BEBIDAS ==========

    @Transactional(readOnly = true)
    public List<BebidaDTO> listarBebidas() {
        return bebidaRepository.findAll().stream()
                .map(this::convertToBebidaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BebidaDTO buscarBebidaPorId(Integer id) {
        return bebidaRepository.findById(id)
                .map(this::convertToBebidaDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<BebidaDTO> buscarBebidasPorNombre(String nombre) {
        Bebida bebida = bebidaRepository.findByNombreBebida(nombre);
        if (bebida != null) {
            return Collections.singletonList(convertToBebidaDTO(bebida));
        }
        return Collections.emptyList();
    }

    @Transactional
    public BebidaDTO guardarBebida(BebidaDTO bebidaDTO) {
        Bebida bebida = convertToBebidaEntity(bebidaDTO);
        bebida = bebidaRepository.save(bebida);
        return convertToBebidaDTO(bebida);
    }

    @Transactional
    public void eliminarBebida(Integer id) {
        bebidaRepository.deleteById(id);
    }

    private BebidaDTO convertToBebidaDTO(Bebida bebida) {
        BebidaDTO dto = new BebidaDTO();
        dto.setId(bebida.getIdBebida());
        dto.setNombre(bebida.getNombreBebida());
        dto.setTipo(bebida.getTipoBebida());
        dto.setPrecio(bebida.getPrecio());
        return dto;
    }

    private Bebida convertToBebidaEntity(BebidaDTO dto) {
        Bebida bebida = new Bebida();
        if (dto.getId() != null) {
            bebida.setIdBebida(dto.getId());
        }
        bebida.setNombreBebida(dto.getNombre());
        bebida.setTipoBebida(dto.getTipo());
        bebida.setPrecio(dto.getPrecio());
        return bebida;
    }

    // ========== OPERACIONES PARA ENTRADAS ==========

    @Transactional(readOnly = true)
    public List<EntradaDTO> listarEntradas() {
        return entradaRepository.findAll().stream()
                .map(this::convertToEntradaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EntradaDTO buscarEntradaPorId(Integer id) {
        return entradaRepository.findById(id)
                .map(this::convertToEntradaDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<EntradaDTO> buscarEntradasPorNombre(String nombre) {
        Entrada entrada = entradaRepository.findByNombreEntrada(nombre);
        if (entrada != null) {
            return Collections.singletonList(convertToEntradaDTO(entrada));
        }
        return Collections.emptyList();
    }

    @Transactional
    public EntradaDTO guardarEntrada(EntradaDTO entradaDTO) {
        Entrada entrada = convertToEntradaEntity(entradaDTO);
        entrada = entradaRepository.save(entrada);
        return convertToEntradaDTO(entrada);
    }

    @Transactional
    public void eliminarEntrada(Integer id) {
        entradaRepository.deleteById(id);
    }

    private EntradaDTO convertToEntradaDTO(Entrada entrada) {
        EntradaDTO dto = new EntradaDTO();
        dto.setId(entrada.getIdEntrada());
        dto.setNombre(entrada.getNombreEntrada());
        dto.setPrecio(entrada.getPrecio());
        return dto;
    }

    private Entrada convertToEntradaEntity(EntradaDTO dto) {
        Entrada entrada = new Entrada();
        if (dto.getId() != null) {
            entrada.setIdEntrada(dto.getId());
        }
        entrada.setNombreEntrada(dto.getNombre());
        entrada.setPrecio(dto.getPrecio());
        return entrada;
    }

    // ========== MÉTODOS PARA INTEGRACIÓN CON PEDIDOS ==========

    @Transactional(readOnly = true)
    public Comida obtenerComidaPorNombre(String nombre) {
        return comidaRepository.findByNombreComida(nombre);
    }

    @Transactional(readOnly = true)
    public Bebida obtenerBebidaPorNombre(String nombre) {
        return bebidaRepository.findByNombreBebida(nombre);
    }

    @Transactional(readOnly = true)
    public Entrada obtenerEntradaPorNombre(String nombre) {
        return entradaRepository.findByNombreEntrada(nombre);
    }

    @Transactional(readOnly = true)
    public List<Comida> obtenerTodasComidas() {
        return comidaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Bebida> obtenerTodasBebidas() {
        return bebidaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Entrada> obtenerTodasEntradas() {
        return entradaRepository.findAll();
    }
}