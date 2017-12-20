#pragma once

#include <vector>

#define EXPORTCDECL extern "C" __declspec(dllexport)

class PositionGenerator
{
	private:
		bool checkPositionTaken(int* positions, int x, int y, int numberPlayers) const;

	public:
		void generateRandomPositions(int* positions, int numberPlayers, int mapSize) const;
};

EXPORTCDECL PositionGenerator* PositionGenerator_new() {
	return new PositionGenerator();
}

EXPORTCDECL void PositionGenerator_delete(PositionGenerator* positionGenerator) {
	delete positionGenerator;
}

EXPORTCDECL void PositionGenerator_generateRandomPositions(PositionGenerator* positionGenerator, int * positions, int numberPlayers, int mapSize) {
	positionGenerator->generateRandomPositions(positions, numberPlayers, mapSize);
}