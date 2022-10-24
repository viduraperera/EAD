using Microsoft.AspNetCore.Mvc;
using FuelManagement.Entities;
using FuelManagement.Repositories;
using FuelManagement.Dtos;

namespace FuelManagement.Controllers;

[ApiController]
[Route("owners")]
public class OwnerController : ControllerBase
{
    private readonly IOwnerRepository<Owner> repository;

    private readonly ILogger<OwnerController> _logger;

    public OwnerController(IOwnerRepository<Owner> repository, ILogger<OwnerController> logger)
    {
        _logger = logger;
        this.repository = repository;
    }

    [HttpGet]
    public async Task<IEnumerable<OwnerDto>> Get()
    {
        var customers = (await repository.GetAllAsync())
            .Select(customer => customer.AsDto());
        return customers;
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<OwnerDto>> GetCustomerAsync(Guid id)
    {
        var owner = await repository.GetByIdAsync(id);

        if (owner is null)
        {
            return NotFound();
        }

        return owner.AsDto();
    }

    [HttpPost("register")]
    public async Task<ActionResult<OwnerDto>> RegisterUserAsync(CreateOwnerDto ownerDto)
    {
        Owner owner = new()
        {
            Id = Guid.NewGuid(),
            Name = ownerDto.Name,
            Email = ownerDto.Email,
            FuelType = ownerDto.FuelType,
        };
        await repository.CreateAsync(owner);
        return Ok();
    }

    [HttpPatch("arrival/{id}")]
    public async Task<ActionResult<OwnerDto>> UpdateArrivalTimeAsync(Guid id, UpdateArrivalTimeCustomerDto updateArrivalTimeCustomerDto)
    {
        var owner = await repository.GetByIdAsync(id);

        if (owner is null)
        {
            return NotFound();
        }

        owner.ArrivalTime = DateTimeOffset.Now;
        owner.status = "Fuel Available";

        await repository.UpdateAsync(owner);

        return owner.AsDto();
    }

    [HttpPatch("finish/{id}")]
    public async Task<ActionResult<OwnerDto>> UpdateDepartureTimeAsync(Guid id, UpdateDepartureTimeCustomerDto updateCustomerDto)
    {
        var owner = await repository.GetByIdAsync(id);

        if (owner is null)
        {
            return NotFound();
        }

        owner.FinishTime = DateTimeOffset.Now;
        owner.status = "Fuel Available";

        await repository.UpdateAsync(owner);

        return owner.AsDto();
    }

    [HttpGet("queue/{id}")]
    public async Task<QueueDto> GetCustomerQueueAsync(Guid id)
    {
        OwnerQueueDetails queue = await repository.getQueueCountById(id);

        List<CustomerDto> fuelPumped = new List<CustomerDto>();

        foreach (CustomerDto customer in queue.customers)
        {
            if (customer.status == "Fuel Pumped")
            {
                fuelPumped.Add(customer);
            }
        }

        TimeSpan elapsedTime = TimeSpan.Zero;
        foreach (var item in fuelPumped)
        {
            elapsedTime += item.DepartureTime - item.ArrivalTime;
        }
        TimeSpan averageTime = elapsedTime.Divide(fuelPumped.Count());
        int count = queue.customers.Count() - fuelPumped.Count();
        TimeSpan estimatedTime = averageTime.Multiply(count);

        QueueDto queueData = new QueueDto(queue._id, estimatedTime, count);
        return queueData;
    }
}
