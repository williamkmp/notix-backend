import { parseArgument } from './scripts/parse_args';
import { createDatabaseConnection } from './scripts/create_database_connection';
import { createCollaborationServer } from './scripts/create_collaboration_server';

export async function main() {
    const argument = parseArgument();
    const databaseConnection = createDatabaseConnection({
        databaseHost: argument.dbHost,
        databasePort: argument.dbPort,
        databaseName: argument.dbName,
        databaseUsername: argument.dbUser,
        databasePassword: argument.dbPass,
    });
    const server = createCollaborationServer({
        serverName: argument.appName,
        serverPort: argument.appPort,
        database: databaseConnection,
        updateDebounce: 3000,
        maxDebounceTime: 5000,
    });
    await server.listen();
    console.log("Collaboration server started");
} 