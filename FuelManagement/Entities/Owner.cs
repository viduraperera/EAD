namespace FuelManagement.Entities
{
    public class Owner: User
    {
        public string FuelType { get; set; } = string.Empty;
        public string Location { get; set; } = string.Empty;
        public DateTime ArrivalTime { get; set; }
        public DateTime FinishTime { get; set; }
    }
}