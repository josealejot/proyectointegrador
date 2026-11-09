const pesos = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
})

export const formatoMoneda = (valor) => pesos.format(Number(valor) || 0)

// "2026-11-09" -> "9 nov 2026"
export function formatoFecha(valor) {
  if (!valor) return ''
  const fecha = new Date(`${valor}T00:00:00`)
  return fecha.toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' })
}

// "09:30:00" -> "09:30"
export const formatoHora = (valor) => String(valor || '').slice(0, 5)

// Quita tildes y pasa a minusculas para que la busqueda no dependa de ellas
export function normalizar(texto) {
  return String(texto ?? '')
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .toLowerCase()
}

// Aplica el buscador y los filtros de un modulo sobre las filas
export function filtrarFilas(filas, modulo, busqueda, filtros) {
  const termino = normalizar(busqueda).trim()
  return filas.filter((fila) => {
    const coincideBusqueda =
      !termino || modulo.buscarEn.some((campo) => normalizar(fila[campo]).includes(termino))
    const coincideFiltros = (modulo.filtros || []).every((filtro) => {
      const elegido = filtros[filtro.campo]
      return !elegido || String(fila[filtro.campo]) === elegido
    })
    return coincideBusqueda && coincideFiltros
  })
}
