function gaussianRand() {
    const iterations = 6
    let rand = 0;

    for (let i = 0; i < iterations; i++) {
        rand += Math.random();
    }

    return rand / iterations;
}

export function gaussianRandom(min: number, max: number, decimal_places: number = 2) {
    let random = min + gaussianRand() * (max - min);
    return random.toFixed(decimal_places)
}

export function uniformRandom(min: number, max: number, decimal_places: number = 2) {
    let random = min + Math.random() * (max - min);
    return random.toFixed(decimal_places)
}

export function gaussianRandomInt(min, max) {
    return Math.floor(min + gaussianRand() * (max - min + 1));
}

export function uniformRandomInt(min, max) {
    return Math.floor(min + Math.random() * (max - min + 1));
}