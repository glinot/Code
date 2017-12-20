#pragma once

#include "GeneratorStrategy.h"
#include <cstdint>
#include <vector>
#include <stdexcept>

#define EXPORTCDECL extern "C" __declspec(dllexport)

class MapGenerator
{
	public:
		enum StrategyType
		{
			RANDOM, MIRROR
		};
		std::vector<GeneratorStrategy::TileType> tileTypes;
		GeneratorStrategy* strategy;

	public:
		MapGenerator(GeneratorStrategy::TileType* tileTypes, std::size_t length);
		~MapGenerator();
		void setGenerator(StrategyType strategy);
		void generateMap(GeneratorStrategy::TileType* grid, std::size_t rows, std::size_t columns) const;
};

EXPORTCDECL MapGenerator* MapGenerator_new(GeneratorStrategy::TileType* tileTypes, std::size_t length) {
	return new MapGenerator(tileTypes, length);
}

EXPORTCDECL void MapGenerator_delete(MapGenerator* mapGenerator) {
	delete mapGenerator;
}

EXPORTCDECL void MapGenerator_setGenerator(MapGenerator* mapGenerator, MapGenerator::StrategyType strategy)
{
	mapGenerator->setGenerator(strategy);
}

EXPORTCDECL void MapGenerator_generateMap(MapGenerator* mapGenerator, GeneratorStrategy::TileType* grid, std::size_t rows, std::size_t columns)
{
	mapGenerator->generateMap(grid, rows, columns);
}