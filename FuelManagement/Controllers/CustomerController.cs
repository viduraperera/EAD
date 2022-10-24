using Microsoft.AspNetCore.Mvc;
using FuelManagement.Entities;
using FuelManagement.Repositories;
using FuelManagement.Dtos;

namespace FuelManagement.Controllers;

[ApiController]
[Route("customers")]
public class CustomerController : ControllerBase
{
    private readonly IRepository<Customer> repository;

    private readonly ILogger<CustomerController> _logger;

    public CustomerController(IRepository<Customer> repository, ILogger<CustomerController> logger)
    {
        _logger = logger;
        this.repository = repository;
    }

    [HttpGet]
    public async Task<IEnumerable<CustomerDto>> Get()
    {
        var customers = (await repository.GetAllAsync())
            .Select(customer => customer.AsDto());
        return customers;
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<CustomerDto>> GetCustomerAsync(Guid id)
    {
        var customer = await repository.GetByIdAsync(id);

        if (customer is null)
        {
            return NotFound();
        }

        return customer.AsDto();
    }

    [HttpPost("register")]
    public async Task<ActionResult<CustomerDto>> RegisterUserAsync(CreateCustomerDto customerDto)
    {
        Customer customer = new()
        {
            Id = Guid.NewGuid(),
            Name = customerDto.Name,
            Email = customerDto.Email,
            VehicleType = customerDto.VehicleType,
        };
        await repository.CreateAsync(customer);
        return Ok();
    }

    [HttpPatch("arrival/{id}")]
    public async Task<ActionResult<CustomerDto>> UpdateArrivalTimeAsync(Guid id, UpdateArrivalTimeCustomerDto updateArrivalTimeCustomerDto)
    {
        var customer = await repository.GetByIdAsync(id);

        if (customer is null)
        {
            return NotFound();
        }

        customer.ArrivalTime = DateTimeOffset.Now;
        customer.FuelStationId = updateArrivalTimeCustomerDto.FuelStationId;
        customer.status = "In Queue";

        await repository.UpdateAsync(customer);

        return customer.AsDto();
    }

    [HttpPatch("departure/{id}")]
    public async Task<ActionResult<CustomerDto>> UpdateDepartureTimeAsync(Guid id, UpdateDepartureTimeCustomerDto updateCustomerDto)
    {
        var customer = await repository.GetByIdAsync(id);

        if (customer is null)
        {
            return NotFound();
        }

        customer.DepartureTime = DateTimeOffset.Now;
        customer.status = updateCustomerDto.DidPumpedFuel == true ? "Fuel Pumped" : "No Fuel";

        await repository.UpdateAsync(customer);

        return customer.AsDto();
    }
}
