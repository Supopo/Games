/**
 * 本地存储封装（对应手机版 MMKV）
 */
import storage from '@system.storage'
import { defaultPlayers } from './utils.js'

const KEY_PLAYERS = 'players'
const KEY_ROUND = 'roundCount'
const KEY_LAST_PLAYER = 'lastPlayer'

function parseJson(value, fallback) {
  try {
    if (value === undefined || value === null || value === '') {
      return fallback
    }
    return JSON.parse(value)
  } catch (e) {
    return fallback
  }
}

export function getPlayers() {
  return new Promise((resolve) => {
    storage.get({
      key: KEY_PLAYERS,
      success: (data) => {
        const list = parseJson(data, null)
        if (Array.isArray(list) && list.length > 0) {
          resolve(list)
        } else {
          resolve(defaultPlayers())
        }
      },
      fail: () => {
        resolve(defaultPlayers())
      }
    })
  })
}

export function setPlayers(players) {
  return new Promise((resolve, reject) => {
    storage.set({
      key: KEY_PLAYERS,
      value: JSON.stringify(players || []),
      success: () => resolve(true),
      fail: (err, code) => reject({ err, code })
    })
  })
}

export function getRoundCount() {
  return new Promise((resolve) => {
    storage.get({
      key: KEY_ROUND,
      success: (data) => {
        const n = parseInt(data, 10)
        resolve(isNaN(n) ? 0 : n)
      },
      fail: () => resolve(0)
    })
  })
}

export function setRoundCount(count) {
  return new Promise((resolve, reject) => {
    storage.set({
      key: KEY_ROUND,
      value: String(count || 0),
      success: () => resolve(true),
      fail: (err, code) => reject({ err, code })
    })
  })
}

export function getLastPlayer() {
  return new Promise((resolve) => {
    storage.get({
      key: KEY_LAST_PLAYER,
      success: (data) => {
        resolve(data || '')
      },
      fail: () => resolve('')
    })
  })
}

export function setLastPlayer(name) {
  return new Promise((resolve, reject) => {
    storage.set({
      key: KEY_LAST_PLAYER,
      value: name || '',
      success: () => resolve(true),
      fail: (err, code) => reject({ err, code })
    })
  })
}
