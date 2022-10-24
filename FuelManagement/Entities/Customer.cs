namespace FuelManagement.Entities
{
    public class Customer: User
    {
        public string VehicleType { get; set; } = string.Empty;
        public DateTimeOffset ArrivalTime { get; set; }
        public DateTimeOffset DepartureTime { get; set; }

        public Guid FuelStationId { get; set; }
    }
}