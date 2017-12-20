#pragma once

#define EXPORTCDECL extern "C" __declspec(dllexport)

/// <summary>
/// Structure describing moves informations needed to compute the best moves.
/// </summary>
class MoveManager
{
	public:
		typedef struct {
			int x;
			int y;
			int distanceToEnemy;
			int bonus;
			double cost;
		} Move;

		MoveManager();
		~MoveManager();
		void getBestMoves(int nIn, int nOut, Move* in, Move* out);
		static double getGoodness(const Move& move);
};

bool compareMove(const MoveManager::Move& move1, const MoveManager::Move& move2) {
	return MoveManager::getGoodness(move1) < MoveManager::getGoodness(move2);
}

EXPORTCDECL void MoveManager_getBestMoves(int nIn, int nOut, MoveManager::Move* moves, MoveManager::Move* out) {
	MoveManager moveManager;
	moveManager.getBestMoves(nIn, nOut, moves, out);
}