namespace INSAWorld
{
    /// <summary>
    /// Demo map strategy.
    /// </summary>
    public class DemoMap : MapStrategy
    {
        public DemoMap() : base(6, 5, 4) {}

        public override string ToString()
        {
            return "Demo";
        }
    }
}