#include "MoveManager.h"
#include <vector>
#include <algorithm>

MoveManager::MoveManager() {}

MoveManager::~MoveManager() {}

double MoveManager::getGoodness(const Move& move) {
	return (3 - move.cost)  * 20 + move.distanceToEnemy/2 + move.bonus*10;
}

void MoveManager::getBestMoves(int nIn , int nOut, Move* in, Move* out) {
	std::vector<Move> moves;
	int i = 0;
	while (i < nIn)
		moves.push_back(in[i++]);

	std::sort(moves.begin(), moves.end(), compareMove);

	for (int i = 0; i < nOut && i < nIn; i++) {
		out[i] = moves[i];
	}
		
}