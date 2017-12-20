#pragma once

#include "GeneratorStrategy.h"

class MirrorStrategy : public GeneratorStrategy
{
	public:
		void generate(const std::vector<TileType>& tileTypes, TileType* grid, std::size_t rows, std::size_t columns) const;
};