// Dibuja un campo del formulario segun su tipo: caja de texto, lista desplegable,
// botones de radio, casilla de chequeo, fecha, hora o area de texto.
export default function Campo({ campo, valor, error, opciones = [], deshabilitado, minimo, onCambio }) {
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
