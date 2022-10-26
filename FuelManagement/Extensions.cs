using FuelManagement.Dtos;
using FuelManagement.Entities;

namespace FuelManagement
{
    public static class Extensions
    {
        public static CustomerDtoWithPassword AsPasswordDto(this Customer customer)
        {
            return new CustomerDtoWithPassword(customer.Id, customer.Name, customer.Email, customer.PasswordHash, customer.PasswordSalt, customer.status, customer.VehicleType, customer.ArrivalTime, customer.DepartureTime, customer.FuelStationId);
        }
        public static CustomerDto AsDto(this Customer customer)
        {
            return new CustomerDto(customer.Id, customer.Name, customer.Email, customer.status, customer.VehicleType, customer.ArrivalTime, customer.DepartureTime, customer.FuelStationId);
        }
        public static OwnerDto AsDto(this Owner owner)
        {
            return new OwnerDto(owner.Id, owner.Name, owner.Email, owner.status, owner.FuelType, owner.ArrivalTime, owner.FinishTime);
        }
        public static OwnerDtoWithPassword AsPasswordDto(this Owner owner)
        {
            return new OwnerDtoWithPassword(owner.Id, owner.Name, owner.Email, owner.PasswordHash, owner.PasswordSalt, owner.status, owner.FuelType, owner.ArrivalTime, owner.FinishTime);
        }
    }
}