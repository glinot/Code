#include "RandomStrategy.h"
#include <random>

void RandomStrategy::generate(const std::vector<TileType>& tileTypes, TileType* grid, std::size_t rows, std::size_t columns) const
{
	std::vector<std::size_t> counter(tileTypes.size());
	std::size_t value = rows * columns / tileTypes.size();
	std::size_t remainder = rows * columns - tileTypes.size() * value;
	for (std::size_t i = 0; i < tileTypes.size(); ++i)
		if (i < remainder)
			counter[i] = value + 1;
		else
			counter[i] = value;

	std::random_device rd;
	std::default_random_engine re(rd());

	for (std::size_t i = 0; i < rows; ++i)
		for (std::size_t j = 0; j < columns; ++j)
		{
			std::size_t total = 0;
			for (auto& current : counter)
				total += current;

			std::uniform_int_distribution<std::size_t> randomGenerator(1, total);
			std::size_t random = randomGenerator(rd);

			std::size_t k = 0;
			std::size_t accumulator = 0;
			while (accumulator < random)
				accumulator += counter[k++];
			grid[i * rows + j] = tileTypes[k - 1];
			--counter[k - 1];
		}
}