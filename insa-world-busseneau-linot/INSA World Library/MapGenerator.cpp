#include "MapGenerator.h"
#include "MirrorStrategy.h"
#include "RandomStrategy.h"

MapGenerator::MapGenerator(GeneratorStrategy::TileType* tileTypes, std::size_t length) : tileTypes(length), strategy(new RandomStrategy()) {
	for (std::size_t i = 0; i < length; ++i)
		this->tileTypes[i] = tileTypes[i];
}

MapGenerator::~MapGenerator()
{
	delete strategy;
}

void MapGenerator::setGenerator(StrategyType strategy)
{
	GeneratorStrategy* newStrategy;

	if (strategy == RANDOM)
		newStrategy = new RandomStrategy();
	else if (strategy == MIRROR)
			newStrategy = new MirrorStrategy();
	else
		throw std::invalid_argument("Unknown strategy");

	delete this->strategy;
	this->strategy = newStrategy;
}

void MapGenerator::generateMap(GeneratorStrategy::TileType* grid, std::size_t rows, std::size_t columns) const
{
	strategy->generate(tileTypes, grid, rows, columns);
}