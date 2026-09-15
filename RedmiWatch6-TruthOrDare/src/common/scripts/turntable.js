/**
 * 转盘数据与角度计算（纯 div 扇形）
 * 扇区从正上方起顺时针均分；整体绕圆心旋转
 */

export const WHEEL_COLORS = [
  '#E94484',
  '#6C4DFF',
  '#2EAF6D',
  '#F0A202',
  '#3D8BFF',
  '#C44569',
  '#16A085',
  '#8E44AD'
]

export function normalizeAngle(angle) {
  return ((angle % 360) + 360) % 360
}

export function easeOutQuint(t) {
  const p = 1 - t
  return 1 - p * p * p * p * p
}

/**
 * CSS 顺时针：扇区 i 的中心转到正上方
 */
export function calcTargetAngle(currentAngle, winnerIndex, count, extraRounds) {
  const sector = 360 / count
  const rounds = extraRounds == null ? 4 : extraRounds
  const offset = (Math.random() * 0.3 - 0.15) * sector
  let target = winnerIndex * sector + offset
  const minTarget = currentAngle + rounds * 360
  while (target < minTarget) {
    target += 360
  }
  return target
}

function shortenName(name, count) {
  if (!name) {
    return ''
  }
  const max = count > 6 ? 3 : 4
  if (name.length <= max) {
    return name
  }
  return name.substring(0, max - 1) + '…'
}

/**
 * hold + pie 均分饼图：
 * - hold 每次旋转 i * sector（从正上开始，不再额外 -90）
 * - pie 固定旋转 sector，裁出等宽扇区
 */
export function buildSlices(players, size) {
  const list = players || []
  const n = list.length
  if (n <= 0) {
    return []
  }

  const sector = 360 / n
  const half = size / 2
  const cx = half
  const cy = half
  const labelR = half * 0.62
  const labelW = n > 6 ? 58 : 74
  const labelH = 32
  const origin = '0px ' + half + 'px'

  const slices = []
  for (let i = 0; i < n; i++) {
    const color = WHEEL_COLORS[i % WHEEL_COLORS.length]
    const holdRotate = i * sector

    // 从正上方顺时针到扇区中心
    const midFromTop = (i + 0.5) * sector
    const midRad = (midFromTop * Math.PI) / 180
    const lx = cx + Math.sin(midRad) * labelR - labelW / 2
    const ly = cy - Math.cos(midRad) * labelR - labelH / 2

    slices.push({
      id: i,
      name: shortenName(list[i], n),
      color: color,
      holdStyle: {
        position: 'absolute',
        width: half + 'px',
        height: size + 'px',
        left: half + 'px',
        top: '0px',
        overflow: 'hidden',
        transformOrigin: origin,
        transform: JSON.stringify({ rotate: holdRotate + 'deg' })
      },
      pieStyle: {
        width: half + 'px',
        height: size + 'px',
        backgroundColor: color,
        transformOrigin: origin,
        // 略加重叠 0.4°，避免扇区之间露底缝
        transform: JSON.stringify({ rotate: sector + 0.4 + 'deg' })
      },
      labelStyle: {
        position: 'absolute',
        width: labelW + 'px',
        height: labelH + 'px',
        left: lx + 'px',
        top: ly + 'px',
        backgroundColor: 'rgba(0,0,0,0.4)',
        borderRadius: '8px',
        justifyContent: 'center',
        alignItems: 'center'
      }
    })
  }
  return slices
}

export function buildRotorStyle(angleDeg, size) {
  const half = size / 2
  return {
    width: size + 'px',
    height: size + 'px',
    position: 'absolute',
    left: '0px',
    top: '0px',
    transformOrigin: half + 'px ' + half + 'px',
    transform: JSON.stringify({
      rotate: Number(angleDeg).toFixed(2) + 'deg'
    })
  }
}

export function buildFrameStyle(size) {
  const half = size / 2
  return {
    width: size + 'px',
    height: size + 'px',
    borderRadius: half + 'px'
  }
}

export function buildHubStyle(size) {
  return {
    width: size + 'px',
    height: size + 'px',
    position: 'absolute',
    left: '0px',
    top: '0px',
    justifyContent: 'center',
    alignItems: 'center'
  }
}
