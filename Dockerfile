# syntax=docker.io/docker/dockerfile:1

# Use a minimal Node base image for runtime
FROM node:lts-trixie-slim AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
ENV HOSTNAME="0.0.0.0"
ENV PORT=3000

# Create non-root user
RUN addgroup --system --gid 1001 nodejs && \
    adduser --system --uid 1001 nextjs

# Copy only pre-built artifacts from GitLab CI
# These should come from your pipeline artifacts (.next/standalone, .next/static, public)
COPY --chown=nextjs:nodejs .next/standalone ./
COPY --chown=nextjs:nodejs .next/static ./.next/static
COPY --chown=nextjs:nodejs public ./public

# Switch to non-root user
USER nextjs

EXPOSE 3000

# Start the standalone Next.js server
CMD ["node", "server.js"]
