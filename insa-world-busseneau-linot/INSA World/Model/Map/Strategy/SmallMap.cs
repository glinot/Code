namespace INSAWorld
{
    /// <summary>
    /// Small map strategy.
    /// </summary>
    public class SmallMap : MapStrategy
    {
        public SmallMap() : base(10, 20, 6) {}
        public override string ToString()
        {
            return "Small";
        }
    }
}