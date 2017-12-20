#include "PositionGenerator.h"

bool PositionGenerator::checkPositionTaken(int* positions, int x, int y, int numberPlayers) const {
	for (int p = 0 ; p < numberPlayers ; ++p)
		if (positions[p * 2] == x && positions[p * 2 + 1] == y)
			return true;
	return false;
}

void PositionGenerator::generateRandomPositions(int * positions, int numberPlayers, int mapSize) const
{
	
	int bx = 0;
	int by = 0;
	double distance = -1;

	// init position tab 
	for (int i = 0; i < numberPlayers; i++) {
		positions[i * 2] = -1;
		positions[i * 2 + 1] = -1;
	}

	positions[0] = 0;
	positions[1] =0;
	for (int i = 1; i < numberPlayers; ++i) {
		for (int x = 0; x < mapSize; ++x)
			for (int y = 0; y < mapSize; ++y) {
				double ldistance = 0;
				// compute mean distance to others 
				for (int j = 0; j < i; j++) {
					int xp = positions[j * 2];
					int yp = positions[j * 2 + 1];

					int dx = x - xp;
					int dy = y - yp;
					ldistance += dx * dx + dy * dy;
				}
				ldistance = ldistance / (double) i;

				if (ldistance > distance && !checkPositionTaken(positions, x, y,numberPlayers)) {
					distance = ldistance;
					bx = x;
					by = y;
				}
			}

		positions[2 * i] = bx;
		positions[2 * i + 1] = by;

		distance = -1;
	}
}