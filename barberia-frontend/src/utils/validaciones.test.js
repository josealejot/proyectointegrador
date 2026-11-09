import { describe, expect, it } from 'vitest'
import { filtrarFilas } from './formato.js'
import { hoy, prepararEnvio, validarCampo, validarFormulario } from './validaciones.js'

const nombre = { nombre: 'nomcliente', etiqueta: 'Nombre', tipo: 'texto', requerido: true, min: 3, max: 80 }
const telefono = { nombre: 'telcliente', etiqueta: 'Teléfono', tipo: 'digitos', requerido: true, min: 10, max: 10 }
const correo = { nombre: 'correo', etiqueta: 'Correo', tipo: 'correo', requerido: true, max: 100 }
const precio = { nombre: 'precio', etiqueta: 'Precio', tipo: 'numero', requerido: true, min: 1000, max: 500000 }
const fecha = { nombre: 'fecha', etiqueta: 'Fecha', tipo: 'fecha', requerido: true, noAnteriorAHoy: true }
const hora = { nombre: 'hora', etiqueta: 'Hora', tipo: 'hora', requerido: true, min: '08:00', max: '20:00' }

describe('validarCampo', () => {
  it('no permite un campo obligatorio vacío', () => {
    expect(validarCampo(nombre, '   ')).toBe('El campo Nombre es obligatorio.')
  })

  it('revisa la extensión mínima y máxima del texto', () => {
    expect(validarCampo(nombre, 'Al')).toMatch(/mínimo 3/)
    expect(validarCampo(nombre, 'a'.repeat(81))).toMatch(/máximo 80/)
    expect(validarCampo(nombre, 'Santiago Gómez')).toBe('')
  })

  it('solo acepta números en teléfonos y exige 10 dígitos', () => {
    expect(validarCampo(telefono, '30012abc45')).toMatch(/solo puede tener números/)
    expect(validarCampo(telefono, '300123')).toMatch(/10 caracteres/)
    expect(validarCampo(telefono, '3001234567')).toBe('')
  })

  it('valida el formato del correo', () => {
    expect(validarCampo(correo, 'sin-arroba')).toMatch(/correo válido/)
    expect(validarCampo(correo, 'ana@correo.com')).toBe('')
  })

  it('valida que el precio sea un entero dentro del rango', () => {
    expect(validarCampo(precio, '500')).toMatch(/mínimo/)
    expect(validarCampo(precio, '25000.5')).toMatch(/número entero/)
    expect(validarCampo(precio, '25000')).toBe('')
  })

  it('no permite agendar una cita en una fecha anterior a hoy', () => {
    expect(validarCampo(fecha, '2020-01-01', { esNuevo: true })).toMatch(/anterior a hoy/)
    expect(validarCampo(fecha, hoy(), { esNuevo: true })).toBe('')
  })

  it('permite conservar una fecha pasada al editar una cita ya atendida', () => {
    const contexto = { esNuevo: false, valores: { estado: 'Atendida' } }
    expect(validarCampo(fecha, '2020-01-01', contexto)).toBe('')
  })

  it('exige que la hora esté dentro del horario de atención', () => {
    expect(validarCampo(hora, '07:30')).toMatch(/08:00/)
    expect(validarCampo(hora, '21:00')).toMatch(/20:00/)
    expect(validarCampo(hora, '10:30')).toBe('')
  })

  it('rechaza una opción que no está en la lista', () => {
    const campo = { nombre: 'estado', etiqueta: 'Estado', tipo: 'select', requerido: true }
    const opciones = [{ valor: 'Pendiente', texto: 'Pendiente' }]
    expect(validarCampo(campo, 'Otro', { opciones })).toMatch(/opción válida/)
    expect(validarCampo(campo, 'Pendiente', { opciones })).toBe('')
  })
})

describe('validarFormulario', () => {
  it('devuelve solo los campos con error', () => {
    const errores = validarFormulario([nombre, telefono, correo], {
      nomcliente: 'Santiago Gómez',
      telcliente: '',
      correo: 'malo',
    })
    expect(Object.keys(errores)).toEqual(['telcliente', 'correo'])
  })
})

describe('prepararEnvio', () => {
  it('convierte los valores al tipo que espera el backend', () => {
    const campos = [nombre, precio, { nombre: 'activo', etiqueta: 'Activo', tipo: 'check' }]
    expect(prepararEnvio(campos, { nomcliente: '  Ana  ', precio: '25000', activo: true })).toEqual({
      nomcliente: 'Ana',
      precio: 25000,
      activo: true,
    })
  })
})

describe('filtrarFilas', () => {
  const modulo = {
    buscarEn: ['nomcliente', 'idcliente'],
    filtros: [{ campo: 'activo' }],
  }
  const filas = [
    { idcliente: '1017', nomcliente: 'Santiago Gómez', activo: true },
    { idcliente: '1152', nomcliente: 'Mateo Álvarez', activo: false },
  ]

  it('busca sin importar tildes ni mayúsculas', () => {
    expect(filtrarFilas(filas, modulo, 'alvarez', {})).toHaveLength(1)
    expect(filtrarFilas(filas, modulo, 'GOMEZ', {})[0].idcliente).toBe('1017')
  })

  it('combina el buscador con los filtros', () => {
    expect(filtrarFilas(filas, modulo, '', { activo: 'false' })).toHaveLength(1)
    expect(filtrarFilas(filas, modulo, 'santiago', { activo: 'false' })).toHaveLength(0)
  })
})
