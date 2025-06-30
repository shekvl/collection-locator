/**
 * A normally distributed random number is approximated by computing the average of several uniformly distributed random numbers
 * @returns random number in the interval [0, 1) with approximated gaussian distribution
 */
function gaussianRand() {
    let sum = 0;
    const iterations = 6

    for (let i = 0; i < iterations; i++) {
        sum += Math.random();
    }

    return sum / iterations;
}

/**
 *
 * @param min lower bound (inclusive)
 * @param max upper bound (exclusive)
 * @param decimal_places precission after comma
 * @returns random number in the interval [min, max) with approximated gaussian distribution
 */
export function gaussianRandom(min: number, max: number, decimal_places: number = 2) {
    let random = min + gaussianRand() * (max - min);
    return random.toFixed(decimal_places)
}

/**
 *
 * @param min lower bound (inclusive)
 * @param max upper bound (exclusive)
 * @param decimal_places precission after comma
 * @returns random number in the interval [min, max) with uniform distribution
 */
export function uniformRandom(min: number, max: number, decimal_places: number = 2) {
    let random = min + Math.random() * (max - min);
    return random.toFixed(decimal_places)
}

/**
 *
 * @param min lower bound (inclusive)
 * @param max upper bound (exclusive)
 * @returns random int between `min` and `max` with approximated gaussian distribution
 */
export function gaussianRandomInt(min: number, max: number) {
    return Math.floor(min + gaussianRand() * (max - min + 1));
}

/**
 *
 * @param min lower bound (inclusive)
 * @param max upper bound (exclusive)
 * @returns random int between `min` and `max` with uniform distribution
 */
export function uniformRandomInt(min: number, max: number) {
    return Math.floor(min + Math.random() * (max - min + 1));
}