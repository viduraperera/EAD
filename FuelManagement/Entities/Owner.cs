namespace FuelManagement.Entities
{
    public class Owner: User
    {
        public string FuelType { get; set; } = string.Empty;
        public DateTimeOffset ArrivalTime { get; set; }
        public DateTimeOffset FinishTime { get; set; }
    }
}