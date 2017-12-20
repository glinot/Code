using Microsoft.VisualStudio.TestTools.UnitTesting;
using INSAWorld;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace INSAWorld.Tests
{
    [TestClass]
    public class NewGameBuilderTests
    {
        [TestMethod]
        public void TestPlayerRaceRandomization()
        {
            Game Game = null;
            IGameBuilder builder = NewGameBuilder.Create().
                    SetMap(new DemoMap()).
                    SetPlayerName("Player 1").
                    SetPlayerName("Player 2").
                    SetPlayerName("Player 3");
            IDictionary <Type, IDictionary<string , int>> racesResults =
                new Dictionary<Type, IDictionary<string, int>>();
            IDictionary<String, int> dict = null;
            
            int i = 0;
            while(i++ < 1000)
            {
                Game = builder.Build();
                for(var j=0 ; j < Game.Players.Count; j++)
                {
                    var name = Game.Players[j].Name;
                    var race = Game.Players[j].Race;
                    if (racesResults.ContainsKey(race.GetType()))
                    {
                        dict = racesResults[race.GetType()];
                        if (dict.ContainsKey(name))
                        {
                            dict[name]++;
                        }
                        else
                        {
                            dict[name] = 1;
                        }
                    }
                    else
                    {
                        dict = new Dictionary<String, int>();
                        dict.Add(name,1);
                        racesResults[race.GetType()] = dict;
                    }
                }

            }

            var normalDistribMean = 1.0 / racesResults.Keys.Count;
            foreach(var type in racesResults.Keys)
            {
                foreach(var name in racesResults[type].Keys)
                {
                    var percentage = racesResults[type][name] / (double)racesResults[type].Values.Sum();
                    Assert.IsTrue(normalDistribMean - 0.1 < percentage && percentage < normalDistribMean + 0.1);
                }

            }
            
        }
    }
}