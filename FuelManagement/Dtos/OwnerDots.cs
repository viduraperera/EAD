// using FuelManagement.
using System.ComponentModel.DataAnnotations;
namespace FuelManagement.Dtos
{
    public record OwnerDto(Guid Id, string Name, string Email, string Password, string status, string FuelType, DateTimeOffset ArrivalTime, DateTimeOffset FinishTime);
    public record OwnerDtoWithoutPassword(Guid Id, string Name, string Email, string status, string FuelType, DateTimeOffset ArrivalTime, DateTimeOffset FinishTime);
    public record CreateOwnerDto([Required]string Name, [Required]string Email, string Password, [Required]string FuelType);
    public record UpdateArrivalTimeOwnerDto([Required]Guid FuelStationId);
    public record UpdateDepartureTimeOwnerDto([Required]bool DidPumpedFuel);
    public record OwnerQueueDetails(Guid _id, string Name, CustomerDto[] customers);
    public record QueueDto(Guid Id, TimeSpan estimatedTime, int queue);
}