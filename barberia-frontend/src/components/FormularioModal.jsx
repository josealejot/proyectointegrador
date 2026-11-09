import { X } from 'lucide-react'
import { useEffect, useState } from 'react'
import { mensajeError } from '../api/http.js'
import { formatoHora } from '../utils/formato.js'
import { hoy, prepararEnvio, validarCampo, validarFormulario } from '../utils/validaciones.js'
import Campo from './Campo.jsx'

function valoresIniciales(campos, fila) {
  const valores = {}
  for (const campo of campos) {
    if (!fila) {
      valores[campo.nombre] = campo.inicial ?? (campo.tipo === 'check' ? false : '')
    } else if (campo.tipo === 'check') {
      valores[campo.nombre] = Boolean(fila[campo.nombre])
    } else if (campo.tipo === 'clave') {
      valores[campo.nombre] = ''
    } else if (campo.tipo === 'hora') {
      valores[campo.nombre] = formatoHora(fila[campo.nombre])
    } else {
      valores[campo.nombre] = String(fila[campo.nombre] ?? '')
    }
  }
  return valores
}

// Ventana con el formulario para crear o editar un registro de cualquier modulo
export default function FormularioModal({ modulo, fila, opciones, onGuardar, onCerrar }) {
  const esNuevo = !fila
  // Al crear no se muestran los campos que solo aplican al editar
  const campos = modulo.campos.filter((campo) => !(esNuevo && campo.soloEditar))

  const [valores, setValores] = useState(() => valoresIniciales(campos, fila))
  const [errores, setErrores] = useState({})
  const [errorServidor, setErrorServidor] = useState('')
  const [guardando, setGuardando] = useState(false)

  // Cerrar con la tecla Escape
  useEffect(() => {
    const alPresionar = (evento) => evento.key === 'Escape' && onCerrar()
    window.addEventListener('keydown', alPresionar)
    return () => window.removeEventListener('keydown', alPresionar)
  }, [onCerrar])

  // En un registro nuevo solo se ofrecen clientes, barberos y servicios activos
  function opcionesDe(campo) {
    const lista = opciones[campo.nombre] || []
    if (esNuevo) return lista.filter((opcion) => opcion.activo !== false)
    return lista.map((opcion) =>
      opcion.activo === false ? { ...opcion, texto: `${opcion.texto} (inactivo)` } : opcion,
    )
  }

  function cambiar(campo, valor) {
    const nuevos = { ...valores, [campo.nombre]: valor }
    setValores(nuevos)
    // Si el campo ya tenia error, se vuelve a validar mientras se corrige
    if (errores[campo.nombre]) {
      const mensaje = validarCampo(campo, valor, { esNuevo, valores: nuevos, opciones: opcionesDe(campo) })
      setErrores({ ...errores, [campo.nombre]: mensaje })
    }
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErrorServidor('')

    const opcionesVisibles = Object.fromEntries(campos.map((campo) => [campo.nombre, opcionesDe(campo)]))
    const encontrados = validarFormulario(campos, valores, { esNuevo, opciones: opcionesVisibles })
    setErrores(encontrados)
    if (Object.keys(encontrados).length > 0) {
      // Lleva el cursor al primer campo con error
      document.getElementById(`campo-${Object.keys(encontrados)[0]}`)?.focus()
      return
    }

    try {
      setGuardando(true)
      await onGuardar(prepararEnvio(campos, valores))
    } catch (error) {
      setErrorServidor(mensajeError(error))
      setGuardando(false)
    }
  }

  const titulo = `${esNuevo ? 'Nuevo' : 'Editar'} ${modulo.singular}`.replace('Nuevo cita', 'Nueva cita')

  return (
    <div className="fondo-modal" onMouseDown={(evento) => evento.target === evento.currentTarget && onCerrar()}>
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="titulo-modal">
        <header className="modal-cabecera">
          <h2 id="titulo-modal">{titulo}</h2>
          <button type="button" className="boton-icono" onClick={onCerrar} aria-label="Cerrar">
            <X size={20} />
          </button>
        </header>

        <form onSubmit={enviar} noValidate>
          {errorServidor && (
            <p className="alerta alerta-error" role="alert">
              {errorServidor}
            </p>
          )}

          <div className="rejilla-campos">
            {campos.map((campo) => (
              <Campo
                key={campo.nombre}
                campo={campo}
                valor={valores[campo.nombre]}
                error={errores[campo.nombre]}
                opciones={opcionesDe(campo)}
                // La llave primaria escrita a mano no se puede cambiar al editar
                deshabilitado={guardando || (!esNuevo && modulo.idManual && campo.nombre === modulo.id)}
                minimo={campo.noAnteriorAHoy && esNuevo ? hoy() : undefined}
                onCambio={(valor) => cambiar(campo, valor)}
              />
            ))}
          </div>

          <footer className="modal-pie">
            <button type="button" className="boton boton-secundario" onClick={onCerrar} disabled={guardando}>
              Cancelar
            </button>
            <button type="submit" className="boton boton-primario" disabled={guardando}>
              {guardando ? 'Guardando…' : esNuevo ? `Guardar ${modulo.singular}` : 'Guardar cambios'}
            </button>
          </footer>
        </form>
      </div>
    </div>
  )
}
