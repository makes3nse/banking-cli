package lv.v3nom;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lv.v3nom.application.service.AccountService;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.application.service.TransactionService;
import lv.v3nom.application.service.impl.AccountServiceImpl;
import lv.v3nom.application.service.impl.AuthServiceImpl;
import lv.v3nom.application.service.impl.CustomerServiceImpl;
import lv.v3nom.application.service.impl.TransactionServiceImpl;
import lv.v3nom.cli.SessionManager;
import lv.v3nom.cli.impl.CommandHandler;
import lv.v3nom.cli.impl.InputParser;
import lv.v3nom.cli.impl.MenuRenderer;
import lv.v3nom.cli.impl.SessionManagerImpl;
import lv.v3nom.domain.security.PasswordHasher;
import lv.v3nom.infrastructure.config.IdempotencyConfig;
import lv.v3nom.infrastructure.di.DIContainer;
import lv.v3nom.infrastructure.di.impl.DIContainerImpl;
import lv.v3nom.infrastructure.idempotency.IdempotencyStore;
import lv.v3nom.infrastructure.repository.INMEM.AccountRepository;
import lv.v3nom.infrastructure.repository.INMEM.CustomerRepository;
import lv.v3nom.infrastructure.repository.INMEM.TransactionRepository;
import lv.v3nom.infrastructure.repository.INMEM.impl.AccountRepositoryImpl;
import lv.v3nom.infrastructure.repository.INMEM.impl.CustomerRepositoryImpl;
import lv.v3nom.infrastructure.repository.INMEM.impl.TransactionRepositoryImpl;
import lv.v3nom.infrastructure.security.BCryptPasswordHasher;
import lv.v3nom.infrastructure.security.TokenProvider;
import lv.v3nom.infrastructure.security.TokenStore;
import lv.v3nom.infrastructure.time.DateTimeProvider;
import lv.v3nom.infrastructure.util.gson.adapters.LocalDateTimeAdapter;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

import java.time.LocalDateTime;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DIContainer container = new DIContainerImpl();
        PasswordHasher hasher = new BCryptPasswordHasher();

        // gson
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setFieldNamingStrategy(FieldNamingPolicy.IDENTITY)
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        container.registerInstance(Gson.class, gson);
        Scanner scanner = new Scanner(System.in);

        // repository
        CustomerRepository customerRepository = new CustomerRepositoryImpl(gson, hasher);
        AccountRepository accountRepository = new AccountRepositoryImpl(gson);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(gson);
        TokenStore tokenStore = new TokenStore(gson);
        // singletons
        container.registerInstance(CustomerRepository.class, customerRepository);
        container.registerInstance(AccountRepository.class, accountRepository);
        container.registerInstance(TransactionRepository.class, transactionRepository);
        container.registerInstance(TokenStore.class, tokenStore);

        // security and utils
        container.register(PasswordHasher.class, BCryptPasswordHasher.class);
        container.register(TokenProvider.class, TokenProvider.class);
        container.register(DateTimeProvider.class, SystemDateTimeProvider.class);
        container.register(SessionManager.class, SessionManagerImpl.class);
        container.register(IdempotencyConfig.class, IdempotencyConfig.class);

        // Idempotency store
        container.register(IdempotencyStore.class, IdempotencyStore.class);
        container.register(IdempotencyConfig.class, IdempotencyConfig.class);

        // services
        container.register(AuthService.class, AuthServiceImpl.class);
        container.register(CustomerService.class, CustomerServiceImpl.class);
        container.register(AccountService.class, AccountServiceImpl.class);
        container.register(TransactionService.class, TransactionServiceImpl.class);

        // instantiate
        try {
            MenuRenderer menuRenderer = container.resolve(MenuRenderer.class);
            InputParser inputParser = container.resolve(InputParser.class);
            SessionManager sessionManager = container.resolve(SessionManager.class);
            AccountService accountService = container.resolve(AccountService.class);
            AuthService authService = container.resolve(AuthService.class);
            CustomerService customerService = container.resolve(CustomerService.class);
            TransactionService transactionService = container.resolve(TransactionService.class);

            System.out.println("DI | Success. Instance getClass().getSimpleName: "
                    + menuRenderer.getClass().getSimpleName());
            System.out.println("DI | Success. Instance getClass().getSimpleName: "
                    + inputParser.getClass().getSimpleName());
            System.out.println("DI | Success. Instance getClass().getSimpleName: "
                    + sessionManager.getClass().getSimpleName());
            System.out.println("DI | Success. Instance getClass().getSimpleName: "
                    + accountService.getClass().getSimpleName());
            System.out.println("DI | Success. Instance getClass().getSimpleName: "
                    + authService.getClass().getSimpleName());
            System.out.println("DI | Success. Instance getClass().getSimpleName: "
                    + customerService.getClass().getSimpleName());
            System.out.println("DI | Success. Instance getClass().getSimpleName: "
                    + transactionService.getClass().getSimpleName());

            scanner.nextLine();

            if (menuRenderer == null) { System.out.println("menuRenderer is null"); }
            if (inputParser == null) { System.out.println("inputParser is null"); }
            if (sessionManager == null) { System.out.println("sessionManager is null"); }
            if (accountService == null) { System.out.println("accountService is null"); }
            if (authService == null) { System.out.println("authService is null"); }
            if (customerService == null) { System.out.println("customerService is null"); }
            if (transactionService == null) { System.out.println("transactionService is null"); }

            CommandHandler app = new CommandHandler(
                    menuRenderer,
                    inputParser,
                    sessionManager,
                    accountService,
                    authService,
                    customerService,
                    transactionService
            );

            app.run();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
