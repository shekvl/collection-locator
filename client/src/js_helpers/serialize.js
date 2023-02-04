/**
 * Serialize parameter object for request url
 * @param {*} params Object with arrays and simple type values
 * @returns serialized object string (e.g. ?param1=value1&param2=value2)
 */
export default function serialize(params) {
    const maps = []

    for (const key in params) {
        if (params[key])
            if (typeof params[key] === 'object') {
                const map = params[key].map((value) => `${key}=${value}`).join('&')
                maps.push(map)
            } else {
                const map = `${key}=${params[key]}`
                maps.push(map)
            }
    }

    return maps.join('&')
}