// Validaciones de los formularios. Revisan tipo y extension de cada campo
// con las mismas reglas que tiene la tabla en el backend.

const PATRON_CORREO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// Fecha de hoy en formato YYYY-MM-DD (hora local)
export function hoy() {
  const fecha = new Date()
  const mes = String(fecha.getMonth() + 1).padStart(2, '0')
  const dia = String(fecha.getDate()).padStart(2, '0')
  return `${fecha.getFullYear()}-${mes}-${dia}`
}

// Devuelve el mensaje de error de un campo, o '' si el valor es valido.
// contexto: { esNuevo, valores, opciones }
export function validarCampo(campo, valor, contexto = {}) {
  const { esNuevo = true, valores = {}, opciones = [] } = contexto
  const etiqueta = campo.etiqueta

  if (campo.tipo === 'check') {
    return typeof valor === 'boolean' ? '' : `Indica si ${etiqueta.toLowerCase()} aplica.`
  }

  const texto = String(valor ?? '').trim()
  const requerido = campo.requerido || (campo.requeridoAlCrear && esNuevo)

  if (!texto) {
    return requerido ? `El campo ${etiqueta} es obligatorio.` : ''
  }

  switch (campo.tipo) {
    case 'digitos':
      if (!/^\d+$/.test(texto)) return `El campo ${etiqueta} solo puede tener números.`
      return validarLongitud(campo, texto)

    case 'correo':
      if (!PATRON_CORREO.test(texto)) return 'Escribe un correo válido, por ejemplo nombre@correo.com.'
      return validarLongitud(campo, texto)

    case 'numero': {
      const numero = Number(texto)
      if (!Number.isInteger(numero)) return `El campo ${etiqueta} debe ser un número entero.`
      if (campo.min !== undefined && numero < campo.min) return `El valor mínimo de ${etiqueta} es ${campo.min}.`
      if (campo.max !== undefined && numero > campo.max) return `El valor máximo de ${etiqueta} es ${campo.max}.`
      return ''
    }

    case 'select':
    case 'radio':
      if (opciones.length && !opciones.some((opcion) => String(opcion.valor) === texto)) {
        return `Elige una opción válida en ${etiqueta}.`
      }
      return ''

    case 'fecha': {
      if (!/^\d{4}-\d{2}-\d{2}$/.test(texto) || Number.isNaN(new Date(`${texto}T00:00:00`).getTime())) {
        return `El campo ${etiqueta} no es una fecha válida.`
      }
      // Una cita nueva o pendiente no puede quedar antes de hoy
      const debeSerFutura = esNuevo || valores.estado === 'Pendiente'
      if (campo.noAnteriorAHoy && debeSerFutura && texto < hoy()) {
        return `La ${etiqueta.toLowerCase()} no puede ser anterior a hoy.`
      }
      return ''
    }

    case 'hora':
      if (!/^\d{2}:\d{2}$/.test(texto)) return `El campo ${etiqueta} no es una hora válida.`
      if ((campo.min && texto < campo.min) || (campo.max && texto > campo.max)) {
        return `La ${etiqueta.toLowerCase()} debe estar entre las ${campo.min} y las ${campo.max}.`
      }
      return ''

    default:
      // texto, area y clave
      return validarLongitud(campo, campo.tipo === 'clave' ? String(valor) : texto)
  }
}

function validarLongitud(campo, texto) {
  if (campo.min !== undefined && texto.length < campo.min) {
    return campo.min === campo.max
      ? `El campo ${campo.etiqueta} debe tener ${campo.min} caracteres.`
      : `El campo ${campo.etiqueta} debe tener mínimo ${campo.min} caracteres.`
  }
  if (campo.max !== undefined && texto.length > campo.max) {
    return `El campo ${campo.etiqueta} debe tener máximo ${campo.max} caracteres.`
  }
  return ''
}

// Valida todos los campos y devuelve un objeto { nombreCampo: mensaje } solo con los errores
export function validarFormulario(campos, valores, contexto = {}) {
  const errores = {}
  for (const campo of campos) {
    const mensaje = validarCampo(campo, valores[campo.nombre], {
      ...contexto,
      valores,
      opciones: contexto.opciones?.[campo.nombre] || [],
    })
    if (mensaje) errores[campo.nombre] = mensaje
  }
  return errores
}

// Convierte los valores del formulario (texto) al tipo que espera el backend
export function prepararEnvio(campos, valores) {
  const datos = {}
  for (const campo of campos) {
    const valor = valores[campo.nombre]
    if (campo.tipo === 'check') {
      datos[campo.nombre] = Boolean(valor)
    } else if (campo.tipo === 'clave') {
      datos[campo.nombre] = valor || ''
    } else if (campo.tipo === 'numero' || campo.numerico) {
      datos[campo.nombre] = Number(valor)
    } else {
      datos[campo.nombre] = String(valor ?? '').trim()
    }
  }
  return datos
}
