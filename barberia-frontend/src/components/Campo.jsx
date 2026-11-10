// Dibuja un campo del formulario segun su tipo: caja de texto, lista desplegable,
// selector multiple interactivo (multirubro), botones de radio, casilla de chequeo, fecha, hora o area de texto.
import { Plus, X } from 'lucide-react'
import { useState } from 'react'

export default function Campo({ campo, valor, error, opciones = [], deshabilitado, minimo, onCambio }) {
  const [elegidoTemp, setElegidoTemp] = useState('')
  const id = `campo-${campo.nombre}`
  const idError = `${id}-error`
  const idAyuda = `${id}-ayuda`
  const descritoPor = [error ? idError : null, campo.ayuda ? idAyuda : null].filter(Boolean).join(' ') || undefined
  const comunes = {
    id,
    name: campo.nombre,
    disabled: deshabilitado,
    'aria-invalid': error ? 'true' : undefined,
    'aria-describedby': descritoPor,
  }
  const requerido = campo.requerido || campo.requeridoAlCrear

  let control

  if (campo.tipo === 'check') {
    return (
      <div className={`campo campo-check ${campo.ancho ? 'campo-ancho' : ''}`}>
        <label className="interruptor" htmlFor={id}>
          <input
            {...comunes}
            type="checkbox"
            checked={Boolean(valor)}
            onChange={(evento) => onCambio(evento.target.checked)}
          />
          <span>{campo.etiqueta}</span>
        </label>
      </div>
    )
  }

  if (campo.tipo === 'radio') {
    return (
      <fieldset className={`campo campo-radio ${campo.ancho ? 'campo-ancho' : ''}`} aria-describedby={descritoPor}>
        <legend>
          {campo.etiqueta} {requerido && <span className="obligatorio">*</span>}
        </legend>
        <div className="opciones-radio">
          {opciones.map((opcion) => (
            <label key={opcion.valor} className="opcion-radio">
              <input
                type="radio"
                name={campo.nombre}
                value={opcion.valor}
                checked={String(valor) === String(opcion.valor)}
                disabled={deshabilitado}
                onChange={() => onCambio(opcion.valor)}
              />
              <span>{opcion.texto}</span>
            </label>
          ))}
        </div>
        {error && <p className="error-campo" id={idError}>{error}</p>}
      </fieldset>
    )
  }

  if (campo.tipo === 'multirubro') {
    const seleccionados = Array.isArray(valor) ? valor.map(String) : []
    const disponibles = opciones.filter((op) => !seleccionados.includes(String(op.valor)))
    const objetosSeleccionados = seleccionados
      .map((val) => opciones.find((op) => String(op.valor) === val))
      .filter(Boolean)

    const totalPrecio = objetosSeleccionados.reduce((sum, op) => sum + (Number(op.precio) || 0), 0)
    const totalDuracion = objetosSeleccionados.reduce((sum, op) => sum + (Number(op.duracion) || 0), 0)

    function agregar(val) {
      if (!val || seleccionados.includes(String(val))) return
      onCambio([...seleccionados, String(val)])
      setElegidoTemp('')
    }

    function quitar(val) {
      onCambio(seleccionados.filter((item) => item !== String(val)))
    }

    return (
      <div className={`campo ${campo.ancho ? 'campo-ancho' : ''}`}>
        <label htmlFor={id}>
          {campo.etiqueta} {requerido && <span className="obligatorio">*</span>}
        </label>

        <div className="selector-multiple">
          <div className="selector-multiple-barra">
            <select
              {...comunes}
              value={elegidoTemp}
              onChange={(e) => {
                const v = e.target.value
                if (v) agregar(v)
              }}
            >
              <option value="">+ Desplegar para agregar servicio a la cita…</option>
              {disponibles.map((op) => (
                <option key={op.valor} value={op.valor}>
                  {op.texto}
                </option>
              ))}
            </select>
          </div>

          {/* Botones rapidos de servicios disponibles para agregar con un clic */}
          {disponibles.length > 0 && (
            <div className="catalogo-rapido" aria-label="Servicios disponibles para agregar">
              {disponibles.map((op) => (
                <button
                  key={op.valor}
                  type="button"
                  className="chip-disponible"
                  disabled={deshabilitado}
                  onClick={() => agregar(op.valor)}
                  title="Haz clic para agregar este servicio"
                >
                  <Plus size={14} /> <span>{op.texto}</span>
                </button>
              ))}
            </div>
          )}

          {/* Lista de servicios ya agregados a la cita */}
          {objetosSeleccionados.length > 0 ? (
            <div className="servicios-elegidos">
              <ul className="lista-chips-elegidos">
                {objetosSeleccionados.map((op) => (
                  <li key={op.valor} className="chip-elegido">
                    <span>{op.texto}</span>
                    <button
                      type="button"
                      className="chip-quitar"
                      onClick={() => quitar(op.valor)}
                      disabled={deshabilitado}
                      aria-label={`Quitar ${op.texto}`}
                    >
                      <X size={15} />
                    </button>
                  </li>
                ))}
              </ul>
              <div className="resumen-servicios-cita">
                <span>
                  <strong>{objetosSeleccionados.length}</strong>{' '}
                  {objetosSeleccionados.length === 1 ? 'servicio seleccionado' : 'servicios seleccionados'}
                </span>
                {totalPrecio > 0 && (
                  <span className="numero">
                    Total: <strong>${totalPrecio.toLocaleString('es-CO')}</strong> · {totalDuracion} min
                  </span>
                )}
              </div>
            </div>
          ) : (
            <p className="ayuda-campo">Ningún servicio agregado todavía. Elige uno o varios arriba.</p>
          )}
        </div>

        {campo.ayuda && !error && <p className="ayuda-campo" id={idAyuda}>{campo.ayuda}</p>}
        {error && <p className="error-campo" id={idError}>{error}</p>}
      </div>
    )
  }

  if (campo.tipo === 'select') {
    control = (
      <select {...comunes} value={valor} onChange={(evento) => onCambio(evento.target.value)}>
        <option value="">Selecciona…</option>
        {opciones.map((opcion) => (
          <option key={opcion.valor} value={opcion.valor}>
            {opcion.texto}
          </option>
        ))}
      </select>
    )
  } else if (campo.tipo === 'area') {
    control = (
      <textarea
        {...comunes}
        rows={3}
        maxLength={campo.max}
        value={valor}
        onChange={(evento) => onCambio(evento.target.value)}
      />
    )
  } else if (campo.tipo === 'digitos') {
    control = (
      <input
        {...comunes}
        type="text"
        inputMode="numeric"
        autoComplete="off"
        maxLength={campo.max}
        value={valor}
        // Solo deja escribir numeros
        onChange={(evento) => onCambio(evento.target.value.replace(/\D/g, ''))}
      />
    )
  } else if (campo.tipo === 'numero') {
    control = (
      <input
        {...comunes}
        type="number"
        min={campo.min}
        max={campo.max}
        step={1}
        value={valor}
        onChange={(evento) => onCambio(evento.target.value)}
      />
    )
  } else if (campo.tipo === 'fecha') {
    control = (
      <input {...comunes} type="date" min={minimo} value={valor} onChange={(evento) => onCambio(evento.target.value)} />
    )
  } else if (campo.tipo === 'hora') {
    control = (
      <input
        {...comunes}
        type="time"
        min={campo.min}
        max={campo.max}
        value={valor}
        onChange={(evento) => onCambio(evento.target.value)}
      />
    )
  } else {
    const tipos = { correo: 'email', clave: 'password' }
    control = (
      <input
        {...comunes}
        type={tipos[campo.tipo] || 'text'}
        autoComplete={campo.tipo === 'clave' ? 'new-password' : 'off'}
        maxLength={campo.max}
        value={valor}
        onChange={(evento) => onCambio(evento.target.value)}
      />
    )
  }

  return (
    <div className={`campo ${campo.ancho ? 'campo-ancho' : ''}`}>
      <label htmlFor={id}>
        {campo.etiqueta} {requerido && <span className="obligatorio">*</span>}
      </label>
      {control}
      {campo.ayuda && !error && <p className="ayuda-campo" id={idAyuda}>{campo.ayuda}</p>}
      {error && <p className="error-campo" id={idError}>{error}</p>}
    </div>
  )
}
