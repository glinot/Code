using Microsoft.VisualStudio.TestTools.UnitTesting;
using INSAWorld.Model.Game;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace INSAWorld.Model.Game.Tests
{
    [TestClass]
    public class PositionGeneratorTests
    {
        [TestMethod]
        public void PositionGeneratorDiffrentPositions()
        {
            PositionGenerator p = new PositionGenerator();
            Position[] pos = p.GenerateRandomPositions(3, 10);
            for (int i = 0; i < pos.Length; i++)
            {
                for (int j = 0; j < pos.Length; j++)
                {
                    if (i != j)
                    {
                        Assert.IsTrue(pos[i] != pos[j]);
                    }
                }
            }
            
        }

        [TestMethod]
        public void PositionGeneratorInBoundPositions()
        {
            const int mapSize = 10;
            const int nbPlayer = 3;
            PositionGenerator p = new PositionGenerator();
            Position[] pos = p.GenerateRandomPositions(nbPlayer, mapSize);

            foreach (var position in pos)
            {
                Assert.IsTrue(position.X >= 0);
                Assert.IsTrue(position.Y >= 0);
                Assert.IsTrue(position.X < mapSize);
                Assert.IsTrue(position.X < mapSize);
            }
        }


    }
}