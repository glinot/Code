#pragma once

#include <cstddef>
#include <vector>

class GeneratorStrategy
{
	public: 
		typedef unsigned int TileType;

	public :
		virtual void generate(const std::vector<TileType>& tileTypes, TileType* grid, std::size_t rows, std::size_t columns) const = 0;
};