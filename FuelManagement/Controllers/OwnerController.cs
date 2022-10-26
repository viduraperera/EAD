using Microsoft.AspNetCore.Mvc;
using FuelManagement.Entities;
using FuelManagement.Repositories;
using FuelManagement.Dtos;
using FuelManagement.Utilities;

namespace FuelManagement.Controllers;

[ApiController]
[Route("owners")]
public class OwnerController : ControllerBase
{
    private readonly IOwnerRepository<Owner> repository;

    private readonly ILogger<OwnerController> _logger;

    private readonly IConfiguration configuration;

    public OwnerController(IConfiguration configuration, IOwnerRepository<Owner> repository, ILogger<OwnerController> logger)
    {
        _logger = logger;
        this.repository = repository;
        this.configuration = configuration;
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
        var passwordManager = new PasswordUtilities();
        passwordManager.CreatePasswordHash(ownerDto.Password, out byte[] passwordHash, out byte[] passwordSalt);
        Owner owner = new()
        {
            Id = Guid.NewGuid(),
            Name = ownerDto.Name,
            Email = ownerDto.Email,
            FuelType = ownerDto.FuelType,
            Location = ownerDto.Location,
            PasswordHash = passwordHash,
            PasswordSalt = passwordSalt
        };

        await repository.CreateAsync(owner);
        return Ok(owner);
    }

    [HttpPost("login")]
    public async Task<ActionResult<string>> Login(OwnerLogin credentials)
    {
        var owner = await repository.filterByEmail(credentials.email);

        if (owner is null)
        {
            return NotFound();
        }

        var passwordManager = new PasswordUtilities();
        if (!passwordManager.VerifyPasswordHash(credentials.password, owner.PasswordHash, owner.PasswordSalt))
        {
            return BadRequest("Wrong password.");
        }

        string token = passwordManager.CreateToken(owner, configuration.GetSection("AppSettings:Token").Value);
        LoggedInOwnerDto loggedInOwnerDto = new LoggedInOwnerDto(owner.AsDto(), token);
        return Ok(loggedInOwnerDto);
    }

    [HttpPatch("arrival/{id}")]
    public async Task<ActionResult<OwnerDto>> UpdateArrivalTimeAsync(Guid id)
    {
        var owner = await repository.GetByIdAsync(id);

        if (owner is null)
        {
            return NotFound();
        }

        owner.ArrivalTime = DateTime.Now;
        owner.status = "Fuel Available";

        await repository.UpdateAsync(owner);

        return owner.AsDto();
    }

    [HttpPatch("finish/{id}")]
    public async Task<ActionResult<OwnerDto>> UpdateDepartureTimeAsync(Guid id)
    {
        var owner = await repository.GetByIdAsync(id);

        if (owner is null)
        {
            return NotFound();
        }

        owner.FinishTime = DateTime.Now;
        owner.status = "Fuel Not Available";

        await repository.UpdateAsync(owner);

        return owner.AsDto();
    }

    [HttpPatch("{id}")]
    public async Task<ActionResult<OwnerDto>> UpdateOwnerAsync(Guid id, OwnerDto ownerDto)
    {
        var owner = await repository.GetByIdAsync(id);

        if (owner is null)
        {
            return NotFound();
        }

        owner.Name = ownerDto.Name;
        owner.Email = ownerDto.Email;
        owner.FuelType = ownerDto.FuelType;
        owner.Location = ownerDto.Location;
        owner.FinishTime = ownerDto.FinishTime;
        owner.ArrivalTime = ownerDto.ArrivalTime;
        owner.status = ownerDto.status;

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
