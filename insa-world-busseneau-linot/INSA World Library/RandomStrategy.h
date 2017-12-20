#pragma once

#include "GeneratorStrategy.h"

class RandomStrategy : public GeneratorStrategy
{
	public:
		void generate(const std::vector<TileType>& tileTypes, TileType* grid, std::size_t rows, std::size_t columns) const;
};