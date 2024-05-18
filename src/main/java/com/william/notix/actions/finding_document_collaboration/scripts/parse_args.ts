import { parseArgs } from "util";
import type { ServerArgument } from "./type";

export function parseArgument(): ServerArgument {
    const { values } = parseArgs({
        args: Bun.argv,
        strict: true,
        allowPositionals: true,
        options: {
            dbHost: { type: 'string' },
            dbPort: { type: 'string' },
            dbName: { type: 'string' },
            dbUser: { type: 'string' },
            dbPass: { type: 'string' },
            appPort: { type: 'string', },
            appName: { type: 'string', },
        },  
    });

    return {
        dbHost : values.dbHost ?? '127.0.0.1',
        dbPort : parseInt(values.dbPort ?? '5432'),
        dbName : values.dbName ?? 'database',
        dbUser : values.dbUser ?? 'user',
        dbPass : values.dbPass ?? 'password',
        appName : values.appName ?? 'application',
        appPort : parseInt(values.appPort ?? '3000'),
    }
}