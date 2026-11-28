"""Generate original synthesized demo audio; replace WAVs with course assets if supplied."""
from pathlib import Path
import math
import random
import struct
import wave

DEST = Path(__file__).resolve().parents[1] / "app/src/main/res/raw"
RATE = 22050


def save(name, notes, beat, noise=False):
    rng = random.Random(3)
    samples = bytearray()
    for frequency in notes:
        count = int(RATE * beat)
        for i in range(count):
            t = i / RATE
            envelope = min(1, t / 0.012) * min(1, (beat - t) / 0.05)
            value = math.sin(2 * math.pi * frequency * t)
            value += 0.25 * math.sin(4 * math.pi * frequency * t)
            if noise:
                value = 0.5 * value + rng.uniform(-0.5, 0.5)
                envelope *= math.exp(-5 * t / beat)
            samples.extend(struct.pack('<h', int(11000 * envelope * value)))
    with wave.open(str(DEST / (name + '.wav')), 'wb') as output:
        output.setparams((1, 2, RATE, 0, 'NONE', 'not compressed'))
        output.writeframes(samples)


if __name__ == '__main__':
    DEST.mkdir(parents=True, exist_ok=True)
    save('bgm', [261.63, 329.63, 392, 523.25, 440, 392, 329.63, 293.66] * 4, 0.25)
    save('bgm_boss', [164.81, 164.81, 196, 174.61, 164.81, 246.94, 220, 196] * 4, 0.18)
    save('bullet_hit', [180], 0.09, True)
    save('bomb_explosion', [70], 0.6, True)
    save('get_supply', [523.25, 659.25, 783.99], 0.1)
    save('game_over', [392, 329.63, 293.66, 196], 0.25)
