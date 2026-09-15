/**
 * 通用工具
 */

export function randomIndex(length) {
  if (!length || length <= 0) {
    return 0
  }
  return Math.floor(Math.random() * length)
}

export function pickOne(list) {
  if (!list || list.length === 0) {
    return ''
  }
  return list[randomIndex(list.length)]
}

export function defaultPlayers() {
  return ['1号玩家', '2号玩家', '3号玩家', '4号玩家', '5号玩家', '6号玩家']
}

export function clampPlayerCount(count) {
  const n = parseInt(count, 10)
  if (isNaN(n) || n < 2) {
    return 2
  }
  if (n > 8) {
    return 8
  }
  return n
}

export function makePlayersByCount(count) {
  const n = clampPlayerCount(count)
  const list = []
  for (let i = 0; i < n; i++) {
    list.push((i + 1) + '号玩家')
  }
  return list
}
