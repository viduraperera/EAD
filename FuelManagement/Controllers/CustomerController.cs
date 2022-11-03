using Microsoft.AspNetCore.Mvc;
using FuelManagement.Entities;
using FuelManagement.Repositories;
using FuelManagement.Dtos;
using FuelManagement.Utilities;

namespace FuelManagement.Controllers;

[ApiController]
[Route("customers")]
public class CustomerController : ControllerBase
{
    private readonly IRepository<Customer> repository;

    private readonly ILogger<CustomerController> _logger;

    private readonly IConfiguration configuration;

    public CustomerController(IConfiguration configuration, IRepository<Customer> repository, ILogger<CustomerController> logger)
    {
        _logger = logger;
        this.repository = repository;
        this.configuration = configuration;
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
        var passwordManager = new PasswordUtilities();
        passwordManager.CreatePasswordHash(customerDto.Password, out byte[] passwordHash, out byte[] passwordSalt);
        Customer customer = new()
        {
            Id = Guid.NewGuid(),
            Name = customerDto.Name,
            Email = customerDto.Email,
            VehicleType = customerDto.VehicleType,
            PasswordHash = passwordHash,
            PasswordSalt = passwordSalt
        };
        await repository.CreateAsync(customer);
        return Ok(customer);
    }

    [HttpPost("login")]
    public async Task<ActionResult<string>> Login(OwnerLogin credentials)
    {
        var customer = await repository.filterByEmail(credentials.email);

        if (customer is null)
        {
            return NotFound();
        }

        var passwordManager = new PasswordUtilities();
        if (!passwordManager.VerifyPasswordHash(credentials.password, customer.PasswordHash, customer.PasswordSalt))
        {
            return BadRequest("Wrong password.");
        }

        string token = passwordManager.CreateToken(customer, configuration.GetSection("AppSettings:Token").Value);
        LoggedInCustomerDto loggedInCustomerDto = new LoggedInCustomerDto(customer.AsDto(), token);
        return Ok(loggedInCustomerDto);
    }

    [HttpPatch("{id}")]
    public async Task<ActionResult<CustomerDto>> UpdateArrivalTimeAsync(Guid id, CustomerDto customerDto)
    {
        var customer = await repository.GetByIdAsync(id);

        if (customer is null)
        {
            return NotFound();
        }

        customer.Name = customerDto.Name;
        customer.Email = customerDto.Email;
        customer.status = customerDto.status;
        customer.VehicleType = customerDto.VehicleType;
        customer.ArrivalTime = customerDto.ArrivalTime;
        customer.DepartureTime = customerDto.DepartureTime;

        await repository.UpdateAsync(customer);

        return customer.AsDto();
    }
    [HttpPatch("arrival/{id}")]
    public async Task<ActionResult<CustomerDto>> UpdateArrivalTimeAsync(Guid id, UpdateArrivalTimeCustomerDto updateArrivalTimeCustomerDto)
    {
        var customer = await repository.GetByIdAsync(id);

        if (customer is null)
        {
            return NotFound();
        }

        customer.ArrivalTime = DateTime.Now;
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

        customer.DepartureTime = DateTime.Now;
        customer.status = updateCustomerDto.DidPumpedFuel == true ? "Fuel Pumped" : "No Fuel";

        await repository.UpdateAsync(customer);

        return customer.AsDto();
    }
}
